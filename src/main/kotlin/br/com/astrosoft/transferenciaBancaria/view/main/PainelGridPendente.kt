package br.com.astrosoft.transferenciaBancaria.view.main

import br.com.astrosoft.AppConfig
import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import br.com.astrosoft.transferenciaBancaria.model.beans.UserSaci
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroPendente
import br.com.astrosoft.transferenciaBancaria.viewmodel.ITransferenciaBancariaView
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.refresh
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.SelectionMode.MULTI
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.BigDecimalField
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.component.textfield.TextFieldVariant.LUMO_ALIGN_RIGHT
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import com.vaadin.flow.data.value.ValueChangeMode.ON_CHANGE
import com.vaadin.flow.dom.DomEvent
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

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
    val colValorTransfEdt = colValorTransfEdt()
    val colAutorizacaoEdt = colAutorizacaoEdt()
    colDepositante()
    colCliente()
    val binder = Binder(TransferenciaBancaria::class.java)
    editor.binder = binder
    val valorField = BigDecimalField().apply {
      this.addThemeVariants(LUMO_ALIGN_RIGHT)
      this.setSizeFull()
      addThemeVariants(TextFieldVariant.LUMO_SMALL)
      this.valueChangeMode = ON_CHANGE
      this.locale = Locale.forLanguageTag("pt-BR")
    }
    val autorizacaoField = TextField().apply {
      this.setSizeFull()
      this.valueChangeMode = ON_CHANGE
      addThemeVariants(TextFieldVariant.LUMO_SMALL)
    }
    binder.forField(valorField)
      .withConverter(BigDecimalToDoubleConverter())
      .bind(TransferenciaBancaria::valorTransfEdt.name)
    binder.forField(autorizacaoField)
      .bind(TransferenciaBancaria::autorizacaoEdt.name)
    
    colValorTransfEdt.editorComponent = valorField
    colAutorizacaoEdt.editorComponent = autorizacaoField
    
    addItemDoubleClickListener {event ->
      editor.editItem(event.item)
      valorField.focus()
    }
    addItemClickListener {
      if(editor.isOpen)
        editor.closeEditor()
    }
    
    editor.addCloseListener {event ->
      view.salvaTransferencia(binder.bean)
      this.refresh()
    }
    element.addEventListener("keyup") {_: DomEvent? -> editor.cancel()}.filter =
      "event.key === 'Escape' || event.key === 'Esc'"
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
        this.onLeftClick {view.desmarcaVendedor(multiSelect())}
      }
      button("Finaliza") {
        icon = VaadinIcon.ARROW_CIRCLE_RIGHT.create()
        addThemeVariants(LUMO_SMALL)
        this.onLeftClick {view.marcaUserTrans(multiSelect().filter {it.autorizacaoEdt != ""})}
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

class BigDecimalToDoubleConverter: Converter<BigDecimal, Double> {
  override fun convertToPresentation(value: Double?, context: ValueContext?): BigDecimal {
    value ?: return BigDecimal.valueOf(0.00)
    return BigDecimal.valueOf(value)
  }
  
  override fun convertToModel(value: BigDecimal?, context: ValueContext?): Result<Double> {
    return Result.ok(value?.toDouble() ?: 0.00)
  }
}