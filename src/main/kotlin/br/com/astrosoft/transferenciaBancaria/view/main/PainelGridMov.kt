package br.com.astrosoft.transferenciaBancaria.view.main

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroMov
import br.com.astrosoft.transferenciaBancaria.viewmodel.ITransferenciaBancariaView
import com.github.mvysny.kaributools.fetchAll
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import java.time.LocalDate

class PainelGridMov(view: ITransferenciaBancariaView, blockUpdate: () -> Unit) :
  PainelGrid<TransferenciaBancaria>(view, blockUpdate) {

  override fun Grid<TransferenciaBancaria>.gridConfig() {
    colLoja()
    colnumPedido()
    colDataPedido()
    colVendedor()
    colNotaFiscal()
    colDataNota()
    colMetodo()
    colValorFrete()
    val colValorNota = colValorNota()
    val colValorTransf = colValorTransf()
    colAutorizacao()
    colCliente()

    this.dataProvider.addDataProviderListener {
      val list = it.source.fetchAll()
      val totalValor = list.sumOf { t -> t.valorNota ?: 0.0 }
      val totalTransf = list.sumOf { t -> t.valorTransf ?: 0.0 }
      colValorNota.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>"))
      colValorTransf.setFooter(Html("<b><font size=4>${totalTransf.format()}</font></b>"))
    }
  }

  override fun filterBar() = FilterBarMov()

  inner class FilterBarMov() : FilterBar(), IFiltroMov {
    lateinit var edtPedido: IntegerField
    lateinit var edtDataInicial: DatePicker
    lateinit var edtDataFinal: DatePicker
    lateinit var edtQuery: TextField

    override fun FilterBar.contentBlock() {
      edtPedido = edtPedido {
        addValueChangeListener { blockUpdate() }
      }
      edtDataInicial = edtDataPedido {
        this.label = "Data Inicial"
        addValueChangeListener { blockUpdate() }
      }
      edtDataFinal = edtDataPedido {
        this.label = "Data Final"
        addValueChangeListener { blockUpdate() }
      }
      edtQuery = edtQuery {
        addValueChangeListener { blockUpdate() }
      }
    }

    override fun numPedido(): Int = edtPedido.value ?: 0
    override fun dataInicial(): LocalDate? = edtDataInicial.value
    override fun dataFinal(): LocalDate? = edtDataFinal.value
    override fun query(): String = edtQuery.value ?: ""
  }
}

