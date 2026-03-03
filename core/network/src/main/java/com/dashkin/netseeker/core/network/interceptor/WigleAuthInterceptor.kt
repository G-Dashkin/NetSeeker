package com.dashkin.netseeker.core.network.interceptor

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

// OkHttp interceptor that attaches a WiGLE Basic Auth header to every request.
// WiGLE uses HTTP Basic Auth where the username is the API Name and the
// password is the API Token, both obtained from the WiGLE account dashboard.
internal class WigleAuthInterceptor(
    private val apiName: String,
    private val apiToken: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials = Credentials.basic(apiName, apiToken)
        val request = chain.request().newBuilder()
            .header("Authorization", credentials)
            .build()
        return chain.proceed(request)
    }
}
