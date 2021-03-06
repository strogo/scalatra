package org.scalatra
package auth

import javax.servlet.http.{HttpServletResponse, HttpSession}

object ScentryAuthStore {

  trait ScentryAuthStore {
    def get: String
    def set(value: String): Unit
    def invalidate: Unit
  }

  class HttpOnlyCookieAuthStore(app: => (ScalatraKernel with CookieSupport), secureOnly: Boolean = false) extends CookieAuthStore(app.cookies, secureOnly) {


    private val SET_COOKIE = "Set-Cookie"

    override def set(value: String) {
      app.response.addHeader(SET_COOKIE, Cookie(Scentry.scentryAuthKey, value)(CookieOptions(secure = secureOnly, httpOnly = true)).toCookieString)
    }

  }

  class CookieAuthStore(cookies: => SweetCookies, secureOnly: Boolean = false) extends ScentryAuthStore {

    def get: String = {
      cookies.get(Scentry.scentryAuthKey) getOrElse ""
    }
    def set(value: String): Unit = {
      cookies.set(Scentry.scentryAuthKey, value)(CookieOptions(secure = secureOnly))
    }
    def invalidate: Unit = {
      cookies -= Scentry.scentryAuthKey
    }
  }

  class SessionAuthStore(session: => HttpSession) extends ScentryAuthStore{

    def get: String = {
      session.getAttribute(Scentry.scentryAuthKey).asInstanceOf[String]
    }
    def set(value: String): Unit = {
      session.setAttribute(Scentry.scentryAuthKey, value)
    }
    def invalidate: Unit = session.invalidate
  }
}
