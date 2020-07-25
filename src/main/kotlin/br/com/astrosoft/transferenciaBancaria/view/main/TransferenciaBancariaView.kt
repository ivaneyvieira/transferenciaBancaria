package br.com.astrosoft.transferenciaBancaria.view.main

import br.com.astrosoft.AppConfig
import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.framework.view.tabGrid
import br.com.astrosoft.transferenciaBancaria.model.beans.TransferenciaBancaria
import br.com.astrosoft.transferenciaBancaria.model.beans.UserSaci
import br.com.astrosoft.transferenciaBancaria.view.layout.TransferenciaBancariaLayout
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroDivergencia
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroEditor
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroFinalizar
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroPedido
import br.com.astrosoft.transferenciaBancaria.viewmodel.IFiltroPendente
import br.com.astrosoft.transferenciaBancaria.viewmodel.ITransferenciaBancariaView
import br.com.astrosoft.transferenciaBancaria.viewmodel.SenhaUsuario
import br.com.astrosoft.transferenciaBancaria.viewmodel.SenhaVendendor
import br.com.astrosoft.transferenciaBancaria.viewmodel.TransferenciaBancariaViewModel
import com.github.mvysny.karibudsl.v10.TabSheet
import com.github.mvysny.karibudsl.v10.bind
import com.github.mvysny.karibudsl.v10.contents
import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.tabSheet
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.TextFieldVariant.LUMO_SMALL
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = TransferenciaBancariaLayout::class)
@PageTitle(AppConfig.title)
@HtmlImport("frontend://styles/shared-styles.html")
class TransferenciaBancariaView: ViewLayout<TransferenciaBancariaViewModel>(), ITransferenciaBancariaView {
  private var tabSheetGrid: TabSheet
  private val gridPedido = PainelGridPedido(this) {viewModel.updateGridPedido()}
  private val gridPendente = PainelGridPendente(this) {viewModel.updateGridPendente()}
  private val gridFinalizar = PainelGridFinalizado(this) {viewModel.updateGridFinalizar()}
  private val gridDivergencia = PainelGridDivergencia(this) {viewModel.updateGridDivergencia()}
  private val gridEditor = PainelGridEditor(this) {viewModel.updateGridEditor()}
  override val viewModel: TransferenciaBancariaViewModel = TransferenciaBancariaViewModel(this)
  
  override fun isAccept() = true
  
  init {
    val user = AppConfig.userSaci as UserSaci
    tabSheetGrid = tabSheet {
      setSizeFull()
      if(user.acl_pedido) tabGrid(TAB_PEDIDO, gridPedido)
      if(user.acl_pendente) tabGrid(TAB_PENDENTE, gridPendente)
      if(user.acl_finalizar) tabGrid(TAB_FINALIZAR, gridFinalizar)
      if(user.acl_divergencia) tabGrid(TAB_DIVERGENCIA, gridDivergencia)
      if(user.acl_editor) tabGrid(TAB_EDITOR, gridEditor)
    }
    when {
      user.acl_pedido      -> viewModel.updateGridPedido()
      user.acl_pendente    -> viewModel.updateGridPendente()
      user.acl_finalizar   -> viewModel.updateGridFinalizar()
      user.acl_divergencia -> viewModel.updateGridDivergencia()
      user.acl_editor      -> viewModel.updateGridEditor()
    }
  }
  
  override fun marcaVendedor(transferenciaBancaria: TransferenciaBancaria?) {
    if(transferenciaBancaria == null)
      showError("Transferencia não selecionada")
    else {
      val form = FormVendedor()
      val vendendor = SenhaVendendor(transferenciaBancaria.vendedor ?: "Não encontrado", "")
      form.binder.bean = vendendor
      showForm("Senha do vendedor", form) {
        val senha = form.binder.bean.senha ?: "#######"
        if(senha == transferenciaBancaria.senhaVendedor)
          viewModel.marcaVendedor(listOf(transferenciaBancaria), true)
        else
          showError("Senha inválida")
      }
    }
  }
  
  override fun marcaUserLink(transferenciaBancaria: List<TransferenciaBancaria>) {
    val userSaci = AppConfig.userSaci as UserSaci
    val form = FormUsuario()
    val usuario = SenhaUsuario(userSaci.login, "")
    form.binder.bean = usuario
    showForm("Senha do Usuário", form) {
      val senha = form.binder.bean.senha ?: "#######"
      if(senha == userSaci.senha)
        viewModel.marcaUserTransf(transferenciaBancaria, true)
      else
        showError("Senha incorreta")
    }
  }
  
  override fun desmarcaVendedor(transferenciaBancaria: List<TransferenciaBancaria>) {
    viewModel.marcaVendedor(transferenciaBancaria, false)
  }
  
  override fun desmarcaUserLink(transferenciaBancaria: List<TransferenciaBancaria>) {
    viewModel.marcaUserTransf(transferenciaBancaria, false)
  }
  
  override fun updateGrid() {
    val painel = (tabSheetGrid.selectedTab?.contents as? PainelGrid<*>) ?: return
    painel.blockUpdate()
  }
  
  override fun updateGridPedido(itens: List<TransferenciaBancaria>) {
    gridPedido.updateGrid(itens)
  }
  
  override fun updateGridPendente(itens: List<TransferenciaBancaria>) {
    gridPendente.updateGrid(itens)
  }
  
  override fun updateGridFinalizar(itens: List<TransferenciaBancaria>) {
    gridFinalizar.updateGrid(itens)
  }
  
  override fun updateGridDivergencia(itens: List<TransferenciaBancaria>) {
    gridDivergencia.updateGrid(itens)
  }
  
  override fun updateGridEditor(itens: List<TransferenciaBancaria>) {
    gridEditor.updateGrid(itens)
  }
  
  override val filtroPedido: IFiltroPedido
    get() = gridPedido.filterBar as IFiltroPedido
  override val filtroPendente: IFiltroPendente
    get() = gridPendente.filterBar as IFiltroPendente
  override val filtroFinalizar: IFiltroFinalizar
    get() = gridFinalizar.filterBar as IFiltroFinalizar
  override val filtroDivergencia: IFiltroDivergencia
    get() = gridDivergencia.filterBar as IFiltroDivergencia
  override val filtroEditor: IFiltroEditor
    get() = gridEditor.filterBar as IFiltroEditor
  
  companion object {
    const val TAB_PEDIDO: String = "Pedido"
    const val TAB_PENDENTE: String = "Pendente"
    const val TAB_FINALIZAR: String = "Finalzar"
    const val TAB_DIVERGENCIA: String = "Devergencia"
    const val TAB_EDITOR: String = "Editor"
  }
}

class FormVendedor: FormLayout() {
  val binder = Binder<SenhaVendendor>(SenhaVendendor::class.java)
  
  init {
    textField("Nome") {
      isEnabled = false
      addThemeVariants(LUMO_SMALL)
      bind(binder).bind(SenhaVendendor::nome)
    }
    
    passwordField("Senha") {
      addThemeVariants(LUMO_SMALL)
      bind(binder).bind(SenhaVendendor::senha)
      this.isAutofocus = true
    }
  }
}

class FormUsuario: FormLayout() {
  val binder = Binder<SenhaUsuario>(SenhaUsuario::class.java)
  
  init {
    textField("Nome") {
      isEnabled = false
      addThemeVariants(LUMO_SMALL)
      bind(binder).bind(SenhaUsuario::nome)
    }
    
    passwordField("Senha") {
      addThemeVariants(LUMO_SMALL)
      bind(binder).bind(SenhaUsuario::senha)
      this.isAutofocus = true
    }
  }
}

