package br.com.astrosoft.transferenciaBancaria.model

import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.util.DB
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import br.com.astrosoft.transferenciaBancaria.model.beans.UserSaci
import java.time.LocalDate
import java.time.LocalTime

class QuerySaci: QueryDB(driver, url, username, password) {
  fun findUser(login: String?): List<UserSaci> {
    login ?: return emptyList()
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql, UserSaci::class) {
      addParameter("login", login)
    }
  }
  
  fun findAllUser(): List<UserSaci> {
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql, UserSaci::class) {
      addParameter("login", "TODOS")
    }
  }
  
  fun updateUser(user: UserSaci) {
    val sql = "/sqlSaci/updateUser.sql"
    script(sql) {
      addOptionalParameter("login", user.login)
      addOptionalParameter("bitAcesso", user.bitAcesso)
      addOptionalParameter("storeno", user.storeno)
    }
  }
  
  fun listaTransferenciaBancaria(storeno: Int): List<TransferenciaBancaria> {
    val sql = "/sqlSaci/transferenciaBancaria.sql"
    val data = LocalDate.now().minusDays(15).toSaciDate()
    return query(sql, TransferenciaBancaria::class) {
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("data", data)
    }
  }
  
  
  fun marcaTransf(loja: Int, numPedido: Int, valorTransfEdt: Double, autorizacao: String) {
    val sql = "/sqlSaci/marcaTransf.sql"
    script(sql) {
      addParameter("storeno", loja)
      addParameter("ordno", numPedido)
      addParameter("valorTransfEdt", valorTransfEdt)
      addParameter("autorizacao", autorizacao)
    }
  }
  
  
  fun marcaVendedor(loja: Int, numPedido: Int, marcaNova: String) {
    val sql = "/sqlSaci/marcaVendedor.sql"
    script(sql) {
      addParameter("storeno", loja)
      addParameter("ordno", numPedido)
      addParameter("marca", marcaNova)
    }
  }
  
  fun marcaUserTransf(loja: Int, numPedido: Int, userLink: Int) {
    val sql = "/sqlSaci/marcaUserTransf.sql"
    script(sql) {
      addParameter("storeno", loja)
      addParameter("ordno", numPedido)
      addParameter("userLink", userLink)
    }
  }
  
  companion object {
    private val db = DB("saci")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
    internal val test = db.test
    val ipServer =
      url.split("/")
        .getOrNull(2)
  }
}

val saci = QuerySaci()