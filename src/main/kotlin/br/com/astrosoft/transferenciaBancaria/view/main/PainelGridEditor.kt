package br.com.astrosoft.transferenciaBancaria.view.main

import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroEditor
import br.com.astrosoft.transferenciaBancaria.viewmodel.ITransferenciaBancariaView
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.IntegerField
import java.time.LocalDate

class PainelGridEditor(view: ITransferenciaBancariaView, blockUpdate: () -> Unit):
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
  
  override fun filterBar() = FilterBarOutros()
  
  inner class FilterBarOutros(): FilterBar(), IFiltroEditor {
    lateinit var edtPedido: IntegerField
    lateinit var edtData: DatePicker
    
    override fun FilterBar.contentBlock() {
      /*
      button("Remover") {
        icon = VaadinIcon.ERASER.create()
        addThemeVariants(LUMO_SMALL)
        onLeftClick {view.desmarcaOutros()}
      }
       */
      edtPedido = edtPedido() {
        addValueChangeListener {blockUpdate()}
      }
      edtData = edtDataPedido() {
        addValueChangeListener {blockUpdate()}
      }
    }
    
    override fun numPedido(): Int = edtPedido.value ?: 0
    override fun data(): LocalDate? = edtData.value
  }
}

