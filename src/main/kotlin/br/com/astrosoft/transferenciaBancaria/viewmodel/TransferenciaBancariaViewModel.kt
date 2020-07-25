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
        filtroPedito(it, filtro)
      }
  }
  
  private fun filtroPedito(it: TransferenciaBancaria,
                           filtro: IFiltroPedido): Boolean {
    return ((it.dataPedido == filtro.data() || filtro.data() == null)
            && (it.numPedido == filtro.numPedido() || filtro.numPedido() == 0)
            && (it.vendedor?.contains(filtro.vendedor()) == true
                || it.empno?.toString() == filtro.vendedor()
                || filtro.vendedor() == ""))
  }
  
  fun updateGridPendente() {
    view.updateGridPendente(listPendente())
  }
  
  private fun listPendente(): List<TransferenciaBancaria> {
    val filtro = view.filtroPendente
    return TransferenciaBancaria.listaPendente()
      .filter {
        (it.dataPedido == filtro.data() || filtro.data() == null)
        && (it.numPedido == filtro.numPedido() || filtro.numPedido() == 0)
        && (it.vendedor?.contains(filtro.vendedor()) == true
            || it.empno?.toString() == filtro.vendedor()
            || filtro.vendedor() == "")
      }
  }
  
  fun updateGridFinalizar() {
    view.updateGridFinalizar(listFinalizado())
  }
  
  private fun listFinalizado(): List<TransferenciaBancaria> {
    val filtro = view.filtroFinalizar
    return TransferenciaBancaria.listaFinalizar()
      .filter {
        (it.dataPedido == filtro.data() || filtro.data() == null)
        && (it.numPedido == filtro.numPedido() || filtro.numPedido() == 0)
      }
  }
  
  fun updateGridDivergencia() {
    view.updateGridDivergencia(listDivergencia())
  }
  
  private fun listDivergencia(): List<TransferenciaBancaria> {
    val filtro = view.filtroDivergencia
    return TransferenciaBancaria.listaDivergencia()
      .filter {
        (it.dataPedido == filtro.data() || filtro.data() == null)
        && (it.numPedido == filtro.numPedido() || filtro.numPedido() == 0)
      }
  }
  
  fun updateGridEditor() {
    view.updateGridEditor(listEditor())
  }
  
  private fun listEditor(): List<TransferenciaBancaria> {
    val filtro = view.filtroEditor
    return TransferenciaBancaria.listaEditor()
      .filter {
        (it.dataPedido == filtro.data() || filtro.data() == null)
        && (it.numPedido == filtro.numPedido() || filtro.numPedido() == 0)
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

interface ITransferenciaBancariaView: IView {
  fun updateGridPedido(itens: List<TransferenciaBancaria>)
  fun updateGridPendente(itens: List<TransferenciaBancaria>)
  fun updateGridFinalizar(itens: List<TransferenciaBancaria>)
  fun updateGridDivergencia(itens: List<TransferenciaBancaria>)
  fun updateGridEditor(itens: List<TransferenciaBancaria>)
  
  val filtroPedido: IFiltroPedido
  val filtroPendente: IFiltroPendente
  val filtroFinalizar: IFiltroFinalizar
  val filtroDivergencia: IFiltroDivergencia
  val filtroEditor: IFiltroEditor
  
  //
  fun marcaVendedor(transferenciaBancaria: TransferenciaBancaria?)
  fun marcaUserLink(transferenciaBancaria: List<TransferenciaBancaria>)
  
  fun desmarcaVendedor(transferenciaBancaria: List<TransferenciaBancaria>)
  fun desmarcaUserLink(transferenciaBancaria: List<TransferenciaBancaria>)
  
  fun updateGrid()
}

data class SenhaVendendor(var nome: String, var senha: String?)

data class SenhaUsuario(var nome: String, var senha: String?)