package io.github.example.pickcolor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.tomgaren.example.pickcolor.databinding.ActivityMainBinding


class MyActivity: AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvLogCat.text = "text1234"

//        tv_test.text = "asdf"

    }
}