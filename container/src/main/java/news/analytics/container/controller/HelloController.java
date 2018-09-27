package news.analytics.container.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping()
    @RequestMapping("/protected")
    public String search() {
        return "Protected resource accessed !";
    }

    @RequestMapping("/public/authenticate")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password");
        if(loginId.equals("bhushan") && password.equals("bkpune")) {
            Cookie cookie = new Cookie("SPRINGBOOT", "AUTH");
            cookie.setDomain("*.technologic.com");
            response.addCookie(cookie);

            // send response as a javascript file, which will redirect to service when loaded on browser
//            String javascript = "<script>window.location.href =\" "+ request.getParameter("service") + "\"</script>";
//            response.getWriter().print(javascript);
        response.sendRedirect(request.getParameter("service"));
        } else {

        }
    }

    @GetMapping()
    @RequestMapping("/public/login")
    public String showLoginPage(HttpServletRequest request, HttpServletResponse response) {
        return "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h2>\n" +
                "    Log-in to your account\n" +
                "</h2>\n" +
                "<form action=\"/public/authenticate\">\n" +
                "Username : <input type=\"text\" name=\"loginId\"><br>\n" +
                "Password : <input type=\"password\" name=\"password\"><br>\n" +
                "<input type=\"hidden\" name=\"service\" value = \""+ request.getParameter("service") +"\">" +
                "<input type=\"submit\">\n" +
                "</form>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>";
    }
}
