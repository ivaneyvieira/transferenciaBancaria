package br.com.astrosoft.transferenciaBancaria.view.main

import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroMov
import br.com.astrosoft.transferenciaBancaria.viewmodel.ITransferenciaBancariaView
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.IntegerField
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
    colValorNota()
    colValorTransf()
    colAutorizacao()
    colCliente()
  }

  override fun filterBar() = FilterBarMov()

  inner class FilterBarMov() : FilterBar(), IFiltroMov {
    lateinit var edtPedido: IntegerField
    lateinit var edtData: DatePicker

    override fun FilterBar.contentBlock() {
      edtPedido = edtPedido() {
        addValueChangeListener { blockUpdate() }
      }
      edtData = edtDataPedido() {
        addValueChangeListener { blockUpdate() }
      }
    }

    override fun numPedido(): Int = edtPedido.value ?: 0
    override fun data(): LocalDate? = edtData.value
  }
}

