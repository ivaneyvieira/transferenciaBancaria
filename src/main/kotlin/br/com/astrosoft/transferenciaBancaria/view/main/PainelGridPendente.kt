package br.com.astrosoft.transferenciaBancaria.view.main

import br.com.astrosoft.AppConfig
import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import br.com.astrosoft.transferenciaBancaria.model.beans.UserSaci
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroPendente
import br.com.astrosoft.transferenciaBancaria.viewmodel.ITransferenciaBancariaView
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode.MULTI
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import java.time.LocalDate

class PainelGridPendente(view: ITransferenciaBancariaView, blockUpdate: () -> Unit):
  PainelGrid<TransferenciaBancaria>(view, blockUpdate) {
  override fun Grid<TransferenciaBancaria>.gridConfig() {
    setSelectionMode(MULTI)
    colLoja()
    colnumPedido()
    colDataPedido()
    colVendedor()
    colMetodo()
    colValorFrete()
    colValorPedido()
    colValorTransfEdt()
    colAutorizacaoEdt()
    colDepositante()
    colCliente()
  }
  
  override fun filterBar() = FilterBarPendente()
  
  inner class FilterBarPendente: FilterBar(), IFiltroPendente {
    lateinit var edtPedido: IntegerField
    lateinit var edtData: DatePicker
    lateinit var edtVendedor: TextField
    
    override fun FilterBar.contentBlock() {
      button("Desmarca") {
        isVisible = (AppConfig.userSaci as? UserSaci)?.admin ?: false
        icon = VaadinIcon.ARROW_CIRCLE_LEFT.create()
        addThemeVariants(LUMO_SMALL)
        onLeftClick {view.desmarcaVendedor(multiSelect())}
      }
      button("Finaliza") {
        icon = VaadinIcon.ARROW_CIRCLE_RIGHT.create()
        addThemeVariants(LUMO_SMALL)
        onLeftClick {view.marcaUserLink(multiSelect().filter {it.autorizacaoEdt != ""})}
      }
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

