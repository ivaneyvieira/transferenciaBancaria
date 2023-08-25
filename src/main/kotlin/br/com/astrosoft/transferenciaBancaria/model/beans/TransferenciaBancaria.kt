package br.com.astrosoft.transferenciaBancaria.model.beans

import br.com.astrosoft.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.transferenciaBancaria.model.saci
import java.time.LocalDate
import java.time.LocalTime

class TransferenciaBancaria(
  val loja: Int,
  val numPedido: Int,
  val dataPedido: LocalDate?,
  val empno: Int?,
  val vendedor: String?,
  val senhaVendedor: String?,
  val metodo: Int?,
  val valorFrete: Double?,
  val valorPedido: Double?,
  val depositante: String?,
  val cliente: String?,
  val nfnoNota: String?,
  val nfseNota: String?,
  val dataNota: LocalDate?,
  var dataTransf: LocalDate?,
  val valorNota: Double?,
  val valorTransf: Double?,
  val autorizacao: String?,
  val status: Int?,
  val marca: String?,
  val userTransf: Int?,
  val banco: Int?,
  var valorTransfEdt: Double?,
  var autorizacaoEdt: String?,
  val dataFat: LocalDate?,
  val nfnoFat: String?,
  val nfseFat: String?,
  val cfoFat: String?,
  val pdvnoFat: Int?,
  val dataEnt: LocalDate?,
  val nfnoEnt: String?,
  val nfseEnt: String?,
  val cfoEnt: String?,
  val pdvnoEnt: Int?,
) {
  val notaFiscalFat: String
    get() = numeroNota(nfnoFat, nfseFat)

  val valorTransfView: Double?
    get() = valorTransfEdt ?: valorTransf
  val autorizacaoView: String?
    get() = autorizacaoEdt ?: autorizacao

  val notaFiscal: String
    get() = numeroNota(nfnoNota, nfseNota)

  val statusPedido
    get() = StatusPedido.values()
      .toList()
      .firstOrNull { it.numero == status }

  private fun numeroNota(nfno: String?, nfse: String?): String {
    nfno ?: return ""
    nfse ?: return nfno
    return when {
      nfno == "" -> ""
      nfse == "" -> nfno
      else -> "$nfno/$nfse"
    }
  }

  fun marcaVendedor(marca: Boolean) {
    saci.marcaVendedor(loja, numPedido, if (marca) "S" else "")
  }

  fun marcaUserTransf(marca: Boolean) {
    val userLink = (AppConfig.userSaci as? UserSaci)?.no ?: return
    saci.marcaUserTransf(loja, numPedido, if (marca) userLink else 0)
  }

  fun filtroData(data: LocalDate?) = filtroData(data, data)

  fun filtroData(dataInicial: LocalDate?, dataFinal: LocalDate?): Boolean {
    val di = dataInicial ?: LocalDate.of(1900, 1, 1)
    val df = dataFinal ?: LocalDate.of(2999, 12, 31)
    dataPedido ?: return dataInicial == null && dataFinal == null
    return dataPedido in di..df
  }

  fun filtroDataFat(dataInicial: LocalDate?, dataFinal: LocalDate?): Boolean {
    val di = dataInicial ?: LocalDate.of(1900, 1, 1)
    val df = dataFinal ?: LocalDate.of(2999, 12, 31)
    dataFat ?: return dataInicial == null && dataFinal == null
    return dataFat.toSaciDate() in di.toSaciDate()..df.toSaciDate()
  }

  fun filtroDataTransf(data: LocalDate?) = filtroDataTransf(data, data)

  fun filtroDataTransf(dataInicial: LocalDate?, dataFinal: LocalDate?): Boolean {
    val di = dataInicial ?: LocalDate.of(1900, 1, 1)
    val df = dataFinal ?: LocalDate.of(2999, 12, 31)
    val data = dataTransf ?: return dataInicial == null && dataFinal == null
    return data.toSaciDate() in di.toSaciDate()..df.toSaciDate()
  }

  fun filtroPedido(num: Int) = numPedido == num || num == 0
  fun filtroVendedor(vendedorStr: String): Boolean {
    val empno = vendedorStr.toIntOrNull()
    return if (empno == null)
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

    private fun TransferenciaBancaria.marcaPedido(): String {
      return marca ?: ""
    }

    fun listaPedido(): List<TransferenciaBancaria> {
      return updateList(storeno).filter {
        it.notaFiscal == ""
            && it.marca != "S"
            && statusValidosPedido.contains(it.status)
      }
    }

    private fun TransferenciaBancaria.edicaoPendente(): Boolean {
      return valorTransfEdt == null || valorTransfEdt == 0.00 ||
          autorizacaoEdt == null || autorizacaoEdt == "" ||
          dataTransf == null
    }

    fun listaPendente(): List<TransferenciaBancaria> {
      return updateList(0).filter {
        it.marcaPedido() == "S"
            &&  it.userPendente()
      }
    }

    private fun TransferenciaBancaria.userPendente(): Boolean {
      return userTransf == null || userTransf == 0
    }

    fun listaFinalizar(): List<TransferenciaBancaria> {
      return updateList(0).filter {
        it.marcaPedido() == "S"
            && !it.userPendente()
      }
    }

    fun listaDivergencia(): List<TransferenciaBancaria> {
      return updateList(storeno).filter {
        it.marcaPedido() == "S"
            && it.divergente()
      }
    }

    fun listaEditor(): List<TransferenciaBancaria> {
      return updateList(storeno).filter {
        it.marcaPedido() == "S"
      }
    }

    fun listaMov(): List<TransferenciaBancaria> {
      return updateList(storeno)
    }

    fun salvaTransferencia(transferenciaBancaria: TransferenciaBancaria) {
      saci.marcaTransf(
        loja = transferenciaBancaria.loja,
        numPedido = transferenciaBancaria.numPedido,
        valorTransfEdt = transferenciaBancaria.valorTransfEdt,
        autorizacao = transferenciaBancaria.autorizacaoEdt,
        dataTransf = transferenciaBancaria.dataTransf,
      )
    }
  }

  private fun divergente(): Boolean {
    return if (banco == 198 || banco == 199) {
      valorNota != valorTransfEdt || valorTransf != valorTransfEdt || autorizacaoEdt != autorizacao
    } else false
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as TransferenciaBancaria

    if (loja != other.loja) return false
    if (numPedido != other.numPedido) return false
    if (dataPedido != other.dataPedido) return false
    if (empno != other.empno) return false
    if (vendedor != other.vendedor) return false
    if (senhaVendedor != other.senhaVendedor) return false
    if (metodo != other.metodo) return false
    if (valorFrete != other.valorFrete) return false
    if (valorPedido != other.valorPedido) return false
    if (depositante != other.depositante) return false
    if (cliente != other.cliente) return false
    if (nfnoNota != other.nfnoNota) return false
    if (nfseNota != other.nfseNota) return false
    if (dataNota != other.dataNota) return false
    if (valorNota != other.valorNota) return false
    if (valorTransf != other.valorTransf) return false
    if (autorizacao != other.autorizacao) return false
    if (status != other.status) return false
    if (marca != other.marca) return false
    if (userTransf != other.userTransf) return false
    if (banco != other.banco) return false
    if (valorTransfEdt != other.valorTransfEdt) return false
    if (autorizacaoEdt != other.autorizacaoEdt) return false

    return true
  }

  override fun hashCode(): Int {
    var result = loja
    result = 31 * result + numPedido
    result = 31 * result + (dataPedido?.hashCode() ?: 0)
    result = 31 * result + (empno ?: 0)
    result = 31 * result + (vendedor?.hashCode() ?: 0)
    result = 31 * result + (senhaVendedor?.hashCode() ?: 0)
    result = 31 * result + (metodo ?: 0)
    result = 31 * result + (valorFrete?.hashCode() ?: 0)
    result = 31 * result + (valorPedido?.hashCode() ?: 0)
    result = 31 * result + (depositante?.hashCode() ?: 0)
    result = 31 * result + (cliente?.hashCode() ?: 0)
    result = 31 * result + (nfnoNota?.hashCode() ?: 0)
    result = 31 * result + (nfseNota?.hashCode() ?: 0)
    result = 31 * result + (dataNota?.hashCode() ?: 0)
    result = 31 * result + (valorNota?.hashCode() ?: 0)
    result = 31 * result + (valorTransf?.hashCode() ?: 0)
    result = 31 * result + (autorizacao?.hashCode() ?: 0)
    result = 31 * result + (status ?: 0)
    result = 31 * result + (marca?.hashCode() ?: 0)
    result = 31 * result + (userTransf ?: 0)
    result = 31 * result + (banco ?: 0)
    result = 31 * result + (valorTransfEdt?.hashCode() ?: 0)
    result = 31 * result + (autorizacaoEdt?.hashCode() ?: 0)
    return result
  }

  fun filtroQuery(query: String): Boolean {
    return (loja == query.toIntOrNull()) ||
        (numPedido == query.toIntOrNull()) ||
        (vendedor?.contains(query, ignoreCase = true) == true) ||
        (cliente?.contains(query, ignoreCase = true) == true) ||
        (nfnoNota == query) ||
        (nfnoFat == query) ||
        (metodo == query.toIntOrNull()) ||
        (valorNota.format().startsWith(query)) ||
        (valorTransf.format().startsWith(query)) ||
        (autorizacao == query) ||
        (query.isBlank())
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