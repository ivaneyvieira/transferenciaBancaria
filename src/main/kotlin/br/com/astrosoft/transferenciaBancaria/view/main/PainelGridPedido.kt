package br.com.astrosoft.transferenciaBancaria.view.main

import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroPedido
import br.com.astrosoft.transferenciaBancaria.viewmodel.ITransferenciaBancariaView
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode.MULTI
import com.vaadin.flow.component.icon.VaadinIcon.ARROW_CIRCLE_RIGHT
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import java.time.LocalDate

class PainelGridPedido(view: ITransferenciaBancariaView, blockUpdate: () -> Unit):
  PainelGrid<TransferenciaBancaria>(view, blockUpdate) {
  override fun Grid<TransferenciaBancaria>.gridConfig() {
    setSelectionMode(MULTI)
    addColumnButton(ARROW_CIRCLE_RIGHT, {transferencia ->
      view.marcaVendedor(transferencia)
    })
    colLoja()
    colnumPedido()
    colDataPedido()
    colVendedor()
    colMetodo()
    colValorFrete()
    colValorPedido()
    colDepositante()
    colCliente()
  }
  
  override fun filterBar() = FilterBarPedido()
  
  inner class FilterBarPedido: FilterBar(), IFiltroPedido {
    lateinit var edtPedido: IntegerField
    lateinit var edtData: DatePicker
    lateinit var edtVendedor: TextField
    
    override fun FilterBar.contentBlock() {
      edtPedido = edtPedido() {
        addValueChangeListener {blockUpdate()}
      }
      edtVendedor = edtVendedor() {
        addValueChangeListener {blockUpdate()}
      }
      edtData = edtDataPedido() {
        addValueChangeListener {blockUpdate()}
      }
    }
    
    override fun numPedido(): Int = edtPedido.value ?: 0
    
    override fun vendedor(): String = edtVendedor.value ?: ""
    
    override fun data(): LocalDate? = edtData.value
  }
}

