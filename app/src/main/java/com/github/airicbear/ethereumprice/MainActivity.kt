package com.github.airicbear.ethereumprice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.airicbear.ethereumprice.databinding.ActivityMainBinding
import com.github.airicbear.ethereumprice.model.EtherPriceResult
import com.github.airicbear.ethereumprice.network.EtherApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchEtherPrice()
        binding.refreshButton.setOnClickListener {
            Log.d("EtherPrice", "Refresh button clicked")
            binding.ethereumPrice.text = getString(R.string.loading)
            fetchEtherPrice()
        }
    }

    private fun fetchEtherPrice() {
        EtherApi.retrofitService.getEtherPrice(BuildConfig.ETHER_SCAN_API_KEY).enqueue(object :
            Callback<EtherPriceResult> {
            override fun onFailure(call: Call<EtherPriceResult>, t: Throwable) {
                binding.ethereumPrice.setTextAppearance(R.style.TextAppearance_AppCompat_Body1)
                binding.ethereumPrice.text = getString(R.string.connection_error)
                Log.d("EtherPrice", call.request().toString())
                Log.d("EtherPrice", t.toString())
            }

            override fun onResponse(call: Call<EtherPriceResult>, response: Response<EtherPriceResult>) {
                val etherResponse = response.body()
                val ethUsdValue = etherResponse?.result?.usdValue?.toDouble()
                val ethValue = NumberFormat.getCurrencyInstance().format(ethUsdValue)
                binding.ethereumPrice.setTextAppearance(R.style.TextAppearance_AppCompat_Display1)
                binding.ethereumPrice.text = ethValue
                Log.d("EtherPrice", "ETH Value: $ethValue")
            }
        })
    }
}