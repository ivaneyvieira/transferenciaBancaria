package br.com.astrosoft.transferenciaBancaria.view.layout

import com.vaadin.flow.router.*
import javax.servlet.http.HttpServletResponse

@ParentLayout(TransferenciaBancariaLayout::class)
class CustomNotFoundTarget : RouteNotFoundError() {
  override fun setErrorParameter(
    event: BeforeEnterEvent,
    parameter: ErrorParameter<NotFoundException>
  ): Int {
    element.text = "My custom not found class!"
    return HttpServletResponse.SC_NOT_FOUND
  }
}