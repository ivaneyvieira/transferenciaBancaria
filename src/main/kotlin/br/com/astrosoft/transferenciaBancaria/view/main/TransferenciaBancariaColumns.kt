package br.com.astrosoft.transferenciaBancaria.view.main

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnLocalDate
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.framework.view.localePtBr
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

fun Grid<TransferenciaBancaria>.colLoja() = addColumnInt(TransferenciaBancaria::loja) {
  setHeader("Lj")
  width = "4em"
}
fun Grid<TransferenciaBancaria>.colnumPedido() = addColumnInt(TransferenciaBancaria::numPedido) {
  setHeader("Pedido")
  width = "7em"
}
fun Grid<TransferenciaBancaria>.colDataPedido() = addColumnLocalDate(TransferenciaBancaria::dataPedido) {
  setHeader("Data")
}
fun Grid<TransferenciaBancaria>.colVendedor() = addColumnString(TransferenciaBancaria::vendedor) {
  setHeader("Vendedor")
}
fun Grid<TransferenciaBancaria>.colMetodo() = addColumnInt(TransferenciaBancaria::metodo) {
  setHeader("Mét")
  width = "5em"
}
fun Grid<TransferenciaBancaria>.colValorFrete() = addColumnDouble(TransferenciaBancaria::valorFrete) {
  setHeader("Frete")
}
fun Grid<TransferenciaBancaria>.colValorPedido() = addColumnDouble(TransferenciaBancaria::valorPedido) {
  setHeader("Valor")
}
fun Grid<TransferenciaBancaria>.colValorTransf() = addColumnDouble(TransferenciaBancaria::valorTransf) {
  setHeader("V. Transf")
}
fun Grid<TransferenciaBancaria>.colValorTransfEdt() = addColumnDouble(TransferenciaBancaria::valorTransfEdt) {
  setHeader("V. Transf")
}
fun Grid<TransferenciaBancaria>.colCliente() = addColumnString(TransferenciaBancaria::cliente) {
  setHeader("Cliente")
}
fun Grid<TransferenciaBancaria>.colDepositante() = addColumnString(TransferenciaBancaria::depositante) {
  setHeader("Depositante")
}
fun Grid<TransferenciaBancaria>.colAutorizacao() = addColumnString(TransferenciaBancaria::autorizacao) {
  setHeader("Autorizacao")
}
fun Grid<TransferenciaBancaria>.colAutorizacaoEdt() = addColumnString(TransferenciaBancaria::autorizacaoEdt) {
  setHeader("Autorizacao")
}
fun Grid<TransferenciaBancaria>.colBanco() = addColumnInt(TransferenciaBancaria::banco) {
  setHeader("Banco")
  width = "5em"
}
fun Grid<TransferenciaBancaria>.colValorNota() = addColumnDouble(TransferenciaBancaria::valorNota) {
  setHeader("V. Nota")
}
fun Grid<TransferenciaBancaria>.colNotaFiscal() = addColumnString(TransferenciaBancaria::notaFiscal) {
  setHeader("NF")
}
fun Grid<TransferenciaBancaria>.colDataNota() = addColumnLocalDate(TransferenciaBancaria::dataNota) {
  setHeader("Data")
}

//Campos de filtro
fun (@VaadinDsl HasComponents).edtPedido(block: (@VaadinDsl IntegerField).() -> Unit = {}) = integerField("Pedido") {
  this.valueChangeMode = ValueChangeMode.TIMEOUT
  this.isAutofocus = true
  addThemeVariants(TextFieldVariant.LUMO_SMALL)
  block()
}

fun (@VaadinDsl HasComponents).edtVendedor(block: (@VaadinDsl TextField).() -> Unit = {}) = textField("Vendedor") {
  this.valueChangeMode = ValueChangeMode.TIMEOUT
  addThemeVariants(TextFieldVariant.LUMO_SMALL)
  block()
}

fun (@VaadinDsl HasComponents).edtDataPedido(block: (@VaadinDsl DatePicker).() -> Unit = {}) = datePicker("Data") {
  localePtBr()
  isClearButtonVisible = true
  element.setAttribute("theme", "small")
  block()
}

