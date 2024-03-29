package br.com.astrosoft.transferenciaBancaria.model.beans

import br.com.astrosoft.framework.spring.IUser
import br.com.astrosoft.transferenciaBancaria.model.saci
import kotlin.math.pow

class UserSaci : IUser {
  var no: Int = 0
  var name: String = ""
  override var login: String = ""
  override var senha: String = ""
  override fun roles(): List<String> {
    val roles = if (admin) listOf("ADMIN") else listOf("USER")
    val rolePedido = if (acl_pedido) listOf("PEDIDO") else listOf()
    val rolePendente = if (acl_pendente) listOf("PENDENTE") else listOf()
    val roleFinalizar = if (acl_finalizar) listOf("FINALIZAR") else listOf()
    val roleFaturado = if (acl_divergencia) listOf("FATURADO") else listOf()
    return roles + rolePedido + rolePendente + roleFinalizar + roleFaturado
  }

  var bitAcesso: Int = 0
  var prntno: Int = 0
  var impressora: String = ""
  var storeno: Int = 0

  //Otiros campos
  var ativo
    get() = (bitAcesso and BIT_ATIVO) != 0 || admin
    set(value) {
      bitAcesso = if (value) bitAcesso or BIT_ATIVO
      else bitAcesso and BIT_ATIVO.inv()
    }
  val admin
    get() = login == "ADM"
  var acl_pedido
    get() = (bitAcesso and BIT_PEDIDO) != 0 || admin
    set(value) {
      bitAcesso = if (value) bitAcesso or BIT_PEDIDO
      else bitAcesso and BIT_PEDIDO.inv()
    }
  var acl_pendente
    get() = (bitAcesso and BIT_PENDENTE) != 0 || admin
    set(value) {
      bitAcesso = if (value) bitAcesso or BIT_PENDENTE
      else bitAcesso and BIT_PENDENTE.inv()
    }
  var acl_finalizar
    get() = (bitAcesso and BIT_FINALIZAR) != 0 || admin
    set(value) {
      bitAcesso = if (value) bitAcesso or BIT_FINALIZAR
      else bitAcesso and BIT_FINALIZAR.inv()
    }
  var acl_divergencia
    get() = (bitAcesso and BIT_DIVERGENCIA) != 0 || admin
    set(value) {
      bitAcesso = if (value) bitAcesso or BIT_DIVERGENCIA
      else bitAcesso and BIT_DIVERGENCIA.inv()
    }
  var acl_editor
    get() = (bitAcesso and BIT_EDITOR) != 0 || admin
    set(value) {
      bitAcesso = if (value) bitAcesso or BIT_EDITOR
      else bitAcesso and BIT_EDITOR.inv()
    }

  var acl_mov
    get() = (bitAcesso and BIT_MOV) != 0 || admin
    set(value) {
      bitAcesso = if (value) bitAcesso or BIT_MOV
      else bitAcesso and BIT_MOV.inv()
    }

  companion object {
    private val BIT_ATIVO = 2.pow(13)
    private val BIT_PEDIDO = 2.pow(0)
    private val BIT_PENDENTE = 2.pow(1)
    private val BIT_FINALIZAR = 2.pow(2)
    private val BIT_DIVERGENCIA = 2.pow(3)
    private val BIT_EDITOR = 2.pow(4)
    private val BIT_MOV = 2.pow(5)

    fun findAll(): List<UserSaci>? {
      return saci.findAllUser()
        .filter { it.ativo }
    }

    fun updateUser(user: UserSaci) {
      saci.updateUser(user)
    }

    fun findUser(login: String?): UserSaci? {
      return saci.findUser(login)
        .firstOrNull()
    }
  }
}

fun Int.pow(e: Int): Int = this.toDouble()
  .pow(e)
  .toInt()
