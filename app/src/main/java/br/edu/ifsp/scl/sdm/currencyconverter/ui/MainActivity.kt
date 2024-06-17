package br.edu.ifsp.scl.sdm.currencyconverter.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.ArrayAdapter
import br.edu.ifsp.scl.sdm.currencyconverter.R
import br.edu.ifsp.scl.sdm.currencyconverter.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.currencyconverter.model.livedata.CurrencyConverterLiveData
import br.edu.ifsp.scl.sdm.currencyconverter.service.ConvertService
import br.edu.ifsp.scl.sdm.currencyconverter.service.CurrienciesService

class MainActivity : AppCompatActivity() {

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val concurrencyServiceIntent by lazy {
        Intent(this, CurrienciesService::class.java)
    }
    private var convertService: ConvertService? = null
    private val convertServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            convertService = (service as ConvertService.ConvertServiceBinder).getService()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            // N/A
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.mainTb.apply { title = getString(R.string.app_name) })

        var fromQuote = ""
        var toQuote = ""
        val currencyAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        with(amb) {
            fromQuoteMactv.apply {
                setAdapter(currencyAdapter)
                setOnItemClickListener { _, _, _, _ ->
                    fromQuote = text.toString()
                }
            }
            toQuoteMactv.apply {
                setAdapter(currencyAdapter)
                setOnItemClickListener { _, _, _, _ ->
                    toQuote = text.toString()
                }
            }
            convertBt.setOnClickListener{
                convertService?.convert(fromQuote, toQuote, amountTiet.text.toString())
            }
        }
        CurrencyConverterLiveData.currenciesLiveData.observe(this) {currencyList ->
            currencyAdapter.clear()
            currencyAdapter.addAll(currencyList.currencies.keys.sorted())
            currencyAdapter.getItem(0)?.also { quote ->
                amb.fromQuoteMactv.setText(quote,false)
                fromQuote = quote
            }

            currencyAdapter.getItem(currencyAdapter.count -1)?.also { quote ->
                amb.toQuoteMactv.setText(quote,false)
                toQuote = quote
            }
        }

        CurrencyConverterLiveData.conversionResultLiveData.observe(this) { conversionResult ->
            with(amb) {
                conversionResult.rates.values.first().rateForAmount.also{
                    resultTiet.setText(it)
                }
            }
        }

        startService(concurrencyServiceIntent)
    }

    override fun onStart() {
        super.onStart()
        Intent(this@MainActivity, ConvertService::class.java).also {intent ->
               bindService(intent, convertServiceConnection, BIND_AUTO_CREATE)
        }
    }
    override fun onStop() {
        super.onStop()
        unbindService(convertServiceConnection)
    }
    override fun onDestroy() {
        super.onDestroy()
        stopService(concurrencyServiceIntent)
    }
}