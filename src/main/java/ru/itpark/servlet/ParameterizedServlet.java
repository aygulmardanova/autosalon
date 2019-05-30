package ru.itpark.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ParameterizedServlet extends HttpServlet {
  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    System.out.println(getInitParameter("init-param"));
    System.out.println(getServletContext().getInitParameter("context-param"));
    resp.getWriter().println("Ok");
  }

  @Override
  public void destroy() {
    System.out.println("destroy");
  }

  @Override
  public void init() {
    System.out.println("init");
  }
}
