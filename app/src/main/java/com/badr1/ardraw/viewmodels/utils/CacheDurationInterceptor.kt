import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * OkHttp Interceptor: Forces a 12-hour max-age cache control on network responses.
 * This ensures Coil respects a defined freshness period regardless of the server's headers.
 */
class ForceCacheDurationInterceptor : Interceptor {

    // 12 hours in seconds
    private val MAX_AGE_SECONDS = 12 * 60 * 60

    private val CACHE_CONTROL = CacheControl.Builder()
        .maxAge(MAX_AGE_SECONDS, TimeUnit.SECONDS)
        .build()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        // Only modify successful network responses
        if (originalResponse.isSuccessful) {
            return originalResponse.newBuilder()
                .removeHeader("Cache-Control") // Clear existing header
                .removeHeader("Pragma")
                .header("Cache-Control", CACHE_CONTROL.toString()) // Add the 12h rule
                .build()
        }
        return originalResponse
    }
}