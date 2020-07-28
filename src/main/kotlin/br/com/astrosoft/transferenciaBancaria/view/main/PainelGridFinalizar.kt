package br.com.astrosoft.transferenciaBancaria.view.main

import br.com.astrosoft.AppConfig
import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import br.com.astrosoft.transferenciaBancaria.model.beans.UserSaci
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroFinalizar
import br.com.astrosoft.transferenciaBancaria.viewmodel.ITransferenciaBancariaView
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode.MULTI
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.IntegerField
import java.time.LocalDate

class PainelGridFinalizar(view: ITransferenciaBancariaView, blockUpdate: () -> Unit): PainelGrid<TransferenciaBancaria>(view, blockUpdate) {
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
    colCliente()
  }
  
  override fun filterBar() = FilterBarFinalizado()
  
  inner class FilterBarFinalizado: FilterBar(), IFiltroFinalizar {
    lateinit var edtPedido: IntegerField
    lateinit var edtData: DatePicker
    
    override fun FilterBar.contentBlock() {
      button("Desmarca") {
        isVisible = (AppConfig.userSaci as? UserSaci)?.admin ?: false
        icon = VaadinIcon.ARROW_CIRCLE_LEFT.create()
        addThemeVariants(LUMO_SMALL)
        onLeftClick {view.desmarcaUserTrans(multiSelect())}
      }
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

