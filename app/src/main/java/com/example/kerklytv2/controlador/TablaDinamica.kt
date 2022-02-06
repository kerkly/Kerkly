package com.example.kerklytv2.controlador

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.kerklytv2.R
import kotlin.collections.ArrayList

class TablaDinamica(tableLayout: TableLayout, context: Context) {
    private lateinit var header: ArrayList<String>
    private var data: MutableList<MutableList<String>>? = null
    private var tableRow: TableRow? = null
    private var txtCell: TextView? = null
    private var indexR = 0
    private var indexC = 0
    private lateinit var tableLayout: TableLayout
    private lateinit var context: Context
    private var multilinea: Boolean = false
    private var color1 = 0
    private var color2 = 0
    private var total = 0

    init {
        this.tableLayout = tableLayout
        this.context = context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color1 = context.getColor(R.color.verdeAltoTabla)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color2 = context.getColor(R.color.verdeBajoTabla)
        }
    }


    fun addHeader(header: ArrayList<String>) {
        this.header = header
        createHeader()
    }

    fun addData(data: ArrayList<ArrayList<String>>?) {
        this.data = data?.toMutableList()
        //createDataTable()
    }

    private fun newRow() {
        tableRow = TableRow(context)
    }

    private fun newCell() {
        txtCell = TextView(context)
        txtCell!!.gravity = Gravity.CENTER
        txtCell!!.textSize = 25f
    }

    private fun createHeader() {
        indexC = 0
        newRow()
        while (indexC < header.size) {
            newCell()
            txtCell!!.text = header[indexC++]
            tableRow!!.addView(txtCell, newTableParams())
        }
        tableLayout.addView(tableRow)
    }

    @SuppressLint("ResourceAsColor")
    fun fondoCabecera(color: Int) {
        indexC = 0
        while (indexC < header.size) {
            txtCell = getCell(0,indexC++)
            txtCell!!.setBackgroundColor(color)
            txtCell!!.setTextColor(R.color.black)
        }
    }

    fun fondoDatos(primerColor: Int, segundoColor: Int) {
        indexR = 0
        while (indexR <= header.size) {
            multilinea =! multilinea
            indexC = 0
            while (indexC <= header.size) {
                txtCell = getCell(indexR, indexC)
                if (multilinea) {
                    txtCell!!.setBackgroundColor(primerColor)
                } else {
                    txtCell!!.setBackgroundColor(segundoColor)
                }
                indexC++
            }
            indexR++
        }
        color1 = primerColor
        color2 = segundoColor
    }

    fun reColorear() {
        indexC = 0
        multilinea =! multilinea
        while (indexC < header.size) {
            txtCell = getCell(data!!.size-1,indexC++)
            if (multilinea) {
                txtCell!!.setBackgroundColor(color1)
            } else {
                txtCell!!.setBackgroundColor(color2)
            }
        }
    }


    private fun getCell(rowIndex: Int, columnIndex: Int): TextView {
        tableRow = getRow(rowIndex)
        return tableRow!!.getChildAt(columnIndex) as TextView
    }

    private fun getRow(index: Int): TableRow {
        return tableLayout.getChildAt(index) as TableRow
    }

    private fun createDataTable() {
        var info: String
        indexR = 0
        while (indexR <= header.size) {
            newRow()
            indexC = 0
            while (indexC <= header.size) {
                newCell()
                val row = data!![indexR-1]
                info = if (indexC < row.size) row[indexC] else ""
                txtCell!!.text = info
                tableRow!!.addView(txtCell, newTableParams())
                indexC++
            }
            tableLayout.addView(tableRow)
            indexR++
        }
    }

    private fun newTableParams(): TableRow.LayoutParams {
        val params = TableRow.LayoutParams()
        params.setMargins(1, 1, 1, 1)
        params.weight = 1f
        return params
    }

    fun addItems(item: ArrayList<String>) {
        var info: String = ""
        data?.add(item)
        indexC = 0
        newRow()
        while (indexC<header.size) {
            newCell()
            info = if (indexC < item.size) item[indexC++] else ""
            //Log.d("mensaje", info)
            txtCell?.text = info
            if (indexC %3 == 0) {
                var t = txtCell?.text.toString()
                var n = t.toInt()

                total += n

                Toast.makeText(context, "Dato: $total", Toast.LENGTH_SHORT).show()

            }
            tableRow?.addView(txtCell,newTableParams())
        }

        //data?.size?.let { tableLayout.addView(tableRow, it) }
        tableLayout.addView(tableRow)//Se quito el -1 despues de size para corregir
        reColorear()
    }

    fun getItems(): Int {
        return data!!.size
    }

    fun eliminar(n: Int) {
        tableLayout.removeAllViews()
        createHeader()

        data?.removeAt(n-1)
        val lista = mutableListOf<MutableList<String>>().apply {
            data?.let { addAll(it) }
        }

        val num = lista.size
        data?.clear()
        Log.d("inidcess", lista.size.toString())


        for (i in 0 ..  num) {
            Log.d("inidce 1", i.toString())
            Log.d("inidce 2", n.toString())
            if (i < n) {
                if (i == num) {
                    break
                }

            } else {
                if (i >= n) {
                    val l = lista[i-1]
                    val menos = i
                    Log.d("menos", menos.toString())
                    l[0] = menos.toString()
                }

            }
        }

        total = 0

        for (i in 0 until lista.size) {
            addItems(lista[i] as ArrayList<String>)
        }
        Log.d("lista", lista.toString())

    }

    fun actualizar (indice: Int, concepto: String, precio: String) {
        tableLayout.removeAllViews()
        createHeader()

        val lista = mutableListOf<MutableList<String>>().apply {
            data?.let { addAll(it) }
        }

        data?.clear()
        val l = lista[indice-1]
        l[1] = concepto
        l[2] = precio

        total = 0
        for (i in 0 until lista.size) {
            addItems(lista[i] as ArrayList<String>)
        }

        Log.d("lista modificada", data.toString())
    }

    fun getTotal (): Double {
        return total.toDouble()
    }

    fun getData(): MutableList<MutableList<String>> {
        return data!!
    }
}