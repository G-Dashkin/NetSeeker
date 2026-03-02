package com.dashkin.netseeker.core.speedtest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

// OkHttp-based [SpeedTestEngine] using Cloudflare speed test endpoints.
// Test sequence:
// 1. **Ping** — [PING_ATTEMPTS] HEAD requests; result is the median latency.
// 2. **Download** — streams [DOWNLOAD_SIZE_BYTES] from `/__down`; emits live throughput.
// 3. **Upload** — POSTs [UPLOAD_SIZE_BYTES] of zeroed bytes to `/__up`; total time measured.
// Speed formula: `Mbps = (bytes × 8) / (elapsedMs × 1_000)`
class SpeedTestEngineImpl(private val client: OkHttpClient) : SpeedTestEngine {

    override fun runTest(
        wifiSsid: String?,
        latitude: Double?,
        longitude: Double?,
    ): Flow<SpeedTestProgress> = flow {
        try {
            val pingMs = measurePing { attempt ->
                emit(SpeedTestProgress.Pinging(attempt, PING_ATTEMPTS))
            }

            val downloadMbps = measureDownload { percent, speedMbps ->
                emit(SpeedTestProgress.Downloading(percent, speedMbps))
            }

            emit(SpeedTestProgress.Uploading(0, 0f))
            val uploadMbps = measureUpload()
            emit(SpeedTestProgress.Uploading(100, uploadMbps))

            emit(
                SpeedTestProgress.Completed(
                    SpeedTestResult(
                        downloadMbps = downloadMbps,
                        uploadMbps = uploadMbps,
                        pingMs = pingMs,
                        timestamp = System.currentTimeMillis(),
                        wifiSsid = wifiSsid,
                        latitude = latitude,
                        longitude = longitude,
                    )
                )
            )
        } catch (e: IOException) {
            emit(SpeedTestProgress.Error(e.message ?: "Network error"))
        } catch (e: Exception) {
            emit(SpeedTestProgress.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun measurePing(onAttempt: suspend (attempt: Int) -> Unit): Int {
        val request = Request.Builder().url(BASE_URL).head().build()
        val latencies = mutableListOf<Long>()

        repeat(PING_ATTEMPTS) { index ->
            onAttempt(index + 1)
            val start = System.currentTimeMillis()
            client.newCall(request).execute().use { }
            latencies += System.currentTimeMillis() - start
        }

        latencies.sort()
        return latencies[latencies.size / 2].toInt()
    }

    private suspend fun measureDownload(
        onProgress: suspend (percent: Int, speedMbps: Float) -> Unit,
    ): Float {
        val request = Request.Builder()
            .url("$BASE_URL/__down?bytes=$DOWNLOAD_SIZE_BYTES")
            .build()

        var totalBytes = 0L
        val startTime = System.currentTimeMillis()
        val buffer = ByteArray(READ_BUFFER_SIZE)

        client.newCall(request).execute().use { response ->
            val body = response.body ?: throw IOException("Empty download response")
            val stream = body.byteStream()

            while (true) {
                val read = stream.read(buffer)
                if (read == -1) break
                totalBytes += read

                val elapsedMs = (System.currentTimeMillis() - startTime).coerceAtLeast(1)
                val percent = ((totalBytes * 100) / DOWNLOAD_SIZE_BYTES).toInt().coerceIn(0, 100)
                onProgress(percent, bytesToMbps(totalBytes, elapsedMs))
            }
        }

        val totalMs = (System.currentTimeMillis() - startTime).coerceAtLeast(1)
        return bytesToMbps(totalBytes, totalMs)
    }

    private fun measureUpload(): Float {
        // Pre-allocate payload so we can measure only network time, not allocation time.
        val payload = ByteArray(UPLOAD_SIZE_BYTES.toInt())
        val body = payload.toRequestBody(OCTET_STREAM_MEDIA_TYPE)

        val request = Request.Builder()
            .url("$BASE_URL/__up")
            .post(body)
            .build()

        val startTime = System.currentTimeMillis()
        client.newCall(request).execute().use { }
        val totalMs = (System.currentTimeMillis() - startTime).coerceAtLeast(1)

        return bytesToMbps(UPLOAD_SIZE_BYTES, totalMs)
    }

    companion object {
        private const val BASE_URL = "https://speed.cloudflare.com"
        private const val DOWNLOAD_SIZE_BYTES = 25_000_000L  // 25 MB
        private const val UPLOAD_SIZE_BYTES = 10_000_000L    // 10 MB
        private const val PING_ATTEMPTS = 5
        private const val READ_BUFFER_SIZE = 32 * 1024       // 32 KB chunks

        private val OCTET_STREAM_MEDIA_TYPE = "application/octet-stream".toMediaType()

        // Converts byte count and elapsed time to Megabits per second.
        fun bytesToMbps(bytes: Long, elapsedMs: Long): Float =
            (bytes * 8f) / (elapsedMs * 1_000f)
    }
}
