package br.com.astrosoft.transferenciaBancaria.model.beans

import br.com.astrosoft.AppConfig
import br.com.astrosoft.transferenciaBancaria.model.saci
import java.time.LocalDate
import java.time.LocalTime

data class TransferenciaBancaria(val loja: Int,
                                 val numPedido: Int,
                                 val dataPedido: LocalDate,
                                 val empno: Int?,
                                 val vendedor: String?,
                                 val senhaVendedor: String,
                                 val metodo: Int,
                                 val valorFrete: Double,
                                 val valorPedido: Double,
                                 val depositante: String,
                                 val cliente: String,
                                 val nfnoNota: String,
                                 val nfseNota: String,
                                 val dataNota: LocalDate,
                                 val valorNota: Double,
                                 val valorTransf: Double,
                                 val autorizacao: String?,
                                 val status: Int,
                                 val marca: String,
                                 val userTransf: Int,
                                 val banco: Int?,
                                 var valorTransfEdt: Double,
                                 var autorizacaoEdt: String
                                ) {
  val notaFiscal: String
    get() = numeroNota(nfnoNota, nfseNota)
  val statusPedido
    get() = StatusPedido.values()
      .toList()
      .firstOrNull {it.numero == status}
  
  private fun numeroNota(nfno: String, nfse: String): String {
    return when {
      nfno == "" -> ""
      nfse == "" -> nfno
      else       -> "$nfno/$nfse"
    }
  }
  
  fun marcaVendedor(marca: Boolean) {
    saci.marcaVendedor(loja, numPedido, if(marca) "S" else "")
  }
  
  fun marcaUserTransf(marca: Boolean) {
    val userLink = (AppConfig.userSaci as? UserSaci)?.no ?: return
    saci.marcaUserTransf(loja, numPedido, if(marca) userLink else 0)
  }
  
  fun filtroData(data: LocalDate?) = dataPedido == data || data == null
  fun filtroPedido(num: Int) = numPedido == num || num == 0
  fun filtroVendedor(vendedorStr: String): Boolean {
    val empno = vendedorStr.toIntOrNull()
    return if(empno == null)
      vendedor?.startsWith(vendedorStr) == true || vendedorStr == ""
    else this.empno == empno
  }
  
  companion object {
    private val storeno
      get() = (AppConfig.userSaci as? UserSaci)?.storeno ?: 0
    private val statusValidosPedido = listOf(1, 2, 8)
    private val list = mutableListOf<TransferenciaBancaria>().apply {
      addAll(saci.listaTransferenciaBancaria(storeno))
    }
    private var time = LocalTime.now()
    
    @Synchronized
    private fun updateList(loja: Int): List<TransferenciaBancaria> {
      return saci.listaTransferenciaBancaria(loja);
    }
    
    fun listaPedido(): List<TransferenciaBancaria> {
      return updateList(storeno).filter {
        it.notaFiscal == ""
        && it.userTransf == 0
        && statusValidosPedido.contains(it.status)
        && it.marca == ""
      }
    }
    
    fun listaPendente(): List<TransferenciaBancaria> {
      return updateList(0).filter {
        it.notaFiscal == ""
        && it.userTransf == 0
        && it.marca == "S"
      }
    }
    
    fun listaFinalizar(): List<TransferenciaBancaria> {
      return updateList(0).filter {
        it.notaFiscal == ""
        && it.userTransf != 0
        && it.marca == "S"
      }
    }
    
    fun listaDivergencia(): List<TransferenciaBancaria> {
      return updateList(storeno).filter {
        it.notaFiscal != ""
        && it.userTransf != 0
        && it.marca == "S"
        && it.divergente()
      }
    }
    
    fun listaEditor(): List<TransferenciaBancaria> {
      return updateList(storeno).filter {
        it.notaFiscal != ""
        && it.userTransf != 0
        && it.marca == "S"
      }
    }
    
    fun salvaTransferencia(transferenciaBancaria: TransferenciaBancaria) {
      saci.marcaTransf(transferenciaBancaria.loja, transferenciaBancaria.numPedido,
                       transferenciaBancaria.valorTransfEdt, transferenciaBancaria.autorizacaoEdt)
    }
  }
  
  private fun divergente(): Boolean {
    return if(banco == 198 || banco == 199) {
      valorNota != valorTransfEdt || valorTransf != valorTransfEdt || autorizacaoEdt != autorizacao
    }
    else false
  }
}

enum class StatusPedido(val numero: Int, val descricao: String) {
  INCLUIDO(0, "Incluído"),
  ORCADO(1, "Orçado"),
  RESERVADO(2, "Reservado"),
  VENDIDO(3, "Vendido"),
  EXPIRADO(4, "Expirado"),
  CANCELADO(5, "Cancelado"),
  RESERVADO_B(6, "Reserva B"),
  TRANSITO(7, "Trânsito"),
  FUTURA(8, "Futura");
  
  override fun toString() = descricao
}