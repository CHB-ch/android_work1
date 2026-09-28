package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 计算器状态
    private var acc: Double? = null      // 已累计的结果
    private var op: String? = null       // 待执行的运算符
    private var freshInput = true        // 是否刚开始输入新数字

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 数字和小数点
        binding.btn0.setOnClickListener { onDigit("0") }
        binding.btn1.setOnClickListener { onDigit("1") }
        binding.btn2.setOnClickListener { onDigit("2") }
        binding.btn3.setOnClickListener { onDigit("3") }
        binding.btn4.setOnClickListener { onDigit("4") }
        binding.btn5.setOnClickListener { onDigit("5") }
        binding.btn6.setOnClickListener { onDigit("6") }
        binding.btn7.setOnClickListener { onDigit("7") }
        binding.btn8.setOnClickListener { onDigit("8") }
        binding.btn9.setOnClickListener { onDigit("9") }
        binding.btnDot.setOnClickListener { onDigit(".") }

        // 运算符（布局里有两个 + 按钮，都当作加号处理）
        binding.btnAdd1.setOnClickListener { onOperator("+") }
        binding.btnAdd2.setOnClickListener { onOperator("+") }
        binding.btnSub.setOnClickListener { onOperator("-") }
        binding.btnMul.setOnClickListener { onOperator("×") }

        // 等号
        binding.btnEq.setOnClickListener { onEquals() }
    }

    private fun onDigit(d: String) {
        val cur = binding.etResult.text.toString()
        if (freshInput) {
            binding.etResult.setText(if (d == ".") "0." else d)
            freshInput = false
        } else {
            if (d == "." && cur.contains(".")) return  // 小数点只能有一个
            binding.etResult.setText(if (cur == "0") d else cur + d)
        }
    }

    private fun onOperator(o: String) {
        val cur = binding.etResult.text.toString().toDoubleOrNull() ?: return
        val a = acc
        if (a == null || op == null) {
            acc = cur
        } else if (!freshInput) {
            // 连续运算：先把上一步算出来
            acc = calculate(a, cur, op!!)
            binding.etResult.setText(format(acc!!))
        }
        op = o
        freshInput = true
        binding.tvInput.text = "${format(acc!!)} $o"
    }

    private fun onEquals() {
        val cur = binding.etResult.text.toString().toDoubleOrNull() ?: return
        val a = acc
        val o = op
        if (a != null && o != null) {
            val result = calculate(a, cur, o)
            binding.tvInput.text = "${format(a)} $o ${format(cur)} ="
            binding.etResult.setText(format(result))
            acc = null
            op = null
            freshInput = true
        }
    }

    private fun calculate(a: Double, b: Double, o: String): Double = when (o) {
        "+" -> a + b
        "-" -> a - b
        "×" -> a * b
        else -> a / b
    }

    // 去掉多余的 .0，比如 6.0 显示成 6
    private fun format(v: Double): String =
        if (v == v.toLong().toDouble() && !v.isInfinite() && !v.isNaN())
            v.toLong().toString()
        else
            v.toString()
}
