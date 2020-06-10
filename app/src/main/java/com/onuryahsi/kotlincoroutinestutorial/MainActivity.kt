package com.onuryahsi.kotlincoroutinestutorial

import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RestrictTo
import com.onuryahsi.kotlincoroutinestutorial.Interface.APIService
import com.onuryahsi.kotlincoroutinestutorial.Model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {


    private val BASE_URL: String = "https://api.github.com"

    private var userDetail: String? = null

    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    private var mAPIService = retrofit.create(APIService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO + Job()).launch {
            loadUserWithLaunch()
        }

    }

    private suspend fun loadData() {
        withContext(Dispatchers.IO) {
            yield()
            println("Suspend function called..")
        }
    }

    private suspend fun loadUserAsync() {
        //supervisorScope()
        withContext(Dispatchers.IO) {
            val deferred = async(Dispatchers.IO) {
                mAPIService.getUser().enqueue(object : Callback<User> {
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Thread.sleep(2000)
                        CoroutineScope(Dispatchers.IO).launch { delay(1500) }
                        helloworld.text = t.message
                    }

                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        Thread.sleep(2000)
                        CoroutineScope(Dispatchers.IO).launch { delay(1500) }
                        if (response.isSuccessful) {
                            helloworld.text = response.body()?.avatar_url
                        } else
                            helloworld.text = response.errorBody()?.toString()
                    }

                })
            }
            try {
                deferred.await()
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Exception : " + e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadUserNormalWay() {
        mAPIService.getUser().enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                helloworld.text = t.message
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                helloworld.text = response.body()?.avatar_url
            }

        })
    }

    private suspend fun loadUserWithLaunch(){
        CoroutineScope(Dispatchers.IO).launch {
            mAPIService.getUser().enqueue(object : Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    helloworld.text = t.message
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    helloworld.text = response.body()?.avatar_url
                }

            })
        }
    }
}
