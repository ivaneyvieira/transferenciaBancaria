package br.com.astrosoft.transferenciaBancaria.viewmodel

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import java.time.LocalDate

class TransferenciaBancariaViewModel(view: ITransferenciaBancariaView): ViewModel<ITransferenciaBancariaView>(view) {
  fun updateGridPedido() {
    view.updateGridPedido(listPedido())
  }
  
  private fun listPedido(): List<TransferenciaBancaria> {
    val filtro = view.filtroPedido
    return TransferenciaBancaria.listaPedido()
      .filter {
        it.filtroData(filtro.data())
        && it.filtroPedido(filtro.numPedido())
        && it.filtroVendedor(filtro.vendedor())
      }
  }
  
  fun updateGridPendente() {
    view.updateGridPendente(listPendente())
  }
  
  private fun listPendente(): List<TransferenciaBancaria> {
    val filtro = view.filtroPendente
    return TransferenciaBancaria.listaPendente()
      .filter {
        it.filtroData(filtro.data())
        && it.filtroPedido(filtro.numPedido())
        && it.filtroVendedor(filtro.vendedor())
      }
  }
  
  fun updateGridFinalizar() {
    view.updateGridFinalizar(listFinalizado())
  }
  
  private fun listFinalizado(): List<TransferenciaBancaria> {
    val filtro = view.filtroFinalizar
    return TransferenciaBancaria.listaFinalizar()
      .filter {
        it.filtroData(filtro.data())
        && it.filtroPedido(filtro.numPedido())
      }
  }
  
  fun updateGridDivergencia() {
    view.updateGridDivergencia(listDivergencia())
  }
  
  private fun listDivergencia(): List<TransferenciaBancaria> {
    val filtro = view.filtroDivergencia
    return TransferenciaBancaria.listaDivergencia()
      .filter {
        it.filtroData(filtro.data())
        && it.filtroPedido(filtro.numPedido())
      }
  }
  
  fun updateGridEditor() {
    view.updateGridEditor(listEditor())
  }

  fun updateGridMov() {
    view.updateGridMov(listEditor())
  }

  private fun listEditor(): List<TransferenciaBancaria> {
    val filtro = view.filtroEditor
    return TransferenciaBancaria.listaEditor()
      .filter {
        it.filtroData(filtro.data())
        && it.filtroPedido(filtro.numPedido())
      }
  }
  
  fun marcaUserTransf(transferencia: List<TransferenciaBancaria>, marca: Boolean) = exec {
    val itens = transferencia
      .ifEmpty {fail("Nenhum item selecionado")}
    itens.forEach {transferenciaBancaria: TransferenciaBancaria ->
      transferenciaBancaria.marcaUserTransf(marca)
    }
    view.updateGrid()
  }
  
  fun marcaVendedor(transferencia: List<TransferenciaBancaria>, marca: Boolean) = exec {
    val itens = transferencia
      .ifEmpty {fail("Nenhum item selecionado")}
    itens.forEach {transferenciaBancaria: TransferenciaBancaria ->
      transferenciaBancaria.marcaVendedor(marca)
    }
    view.updateGrid()
  }
  
  fun salvaTransferencia(bean: TransferenciaBancaria) {
    TransferenciaBancaria.salvaTransferencia(bean)
  }
}

interface IFiltroPedido {
  fun numPedido(): Int
  fun vendedor(): String;
  fun data(): LocalDate?
}

interface IFiltroPendente {
  fun numPedido(): Int
  fun vendedor(): String;
  fun data(): LocalDate?
}

interface IFiltroFinalizar {
  fun numPedido(): Int
  fun data(): LocalDate?
}

interface IFiltroDivergencia {
  fun numPedido(): Int
  fun data(): LocalDate?
}

interface IFiltroEditor {
  fun numPedido(): Int
  fun data(): LocalDate?
}

interface IFiltroMov {
  fun numPedido(): Int
  fun data(): LocalDate?
}

interface ITransferenciaBancariaView: IView {
  fun updateGridPedido(itens: List<TransferenciaBancaria>)
  fun updateGridPendente(itens: List<TransferenciaBancaria>)
  fun updateGridFinalizar(itens: List<TransferenciaBancaria>)
  fun updateGridDivergencia(itens: List<TransferenciaBancaria>)
  fun updateGridEditor(itens: List<TransferenciaBancaria>)
  fun updateGridMov(itens: List<TransferenciaBancaria>)

  val filtroPedido: IFiltroPedido
  val filtroPendente: IFiltroPendente
  val filtroFinalizar: IFiltroFinalizar
  val filtroDivergencia: IFiltroDivergencia
  val filtroEditor: IFiltroEditor
  val filtroMov: IFiltroMov

  //
  fun marcaVendedor(transferenciaBancaria: TransferenciaBancaria?)
  fun marcaUserTrans(transferenciaBancaria: List<TransferenciaBancaria>)
  
  fun desmarcaVendedor(transferenciaBancaria: List<TransferenciaBancaria>)
  fun desmarcaUserTrans(transferenciaBancaria: List<TransferenciaBancaria>)
  
  fun updateGrid()
  fun salvaTransferencia(bean: TransferenciaBancaria?)
}

data class SenhaVendendor(var nome: String, var senha: String?)

data class SenhaUsuario(var nome: String, var senha: String?)