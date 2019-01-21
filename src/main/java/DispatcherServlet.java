import Annotations.Controller;
import Annotations.RequestMapping;
import bo.PokemonType;
import com.fasterxml.jackson.databind.ObjectMapper;
import controller.PokemonTypeController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    private Map<String, Method> uriMappings = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("Getting request for " + req.getRequestURI());

        String uri = req.getRequestURI();
        if(uri.equals("/favicon.ico")) { return; }
        if(!getMappings().containsKey(uri)) {
            resp.sendError(404, "no mapping found for request uri "+uri);
            return;
        }

        Method m = this.getMappingForUri(uri);
        try {
            Object controller = m.getDeclaringClass().getDeclaredConstructor().newInstance();

            Object value;

            if (req.getParameterMap().isEmpty()) {
                value = m.invoke(controller);
            } else {
                value = m.invoke(controller, req.getParameterMap());
            }

            String actualString = new ObjectMapper().writeValueAsString(value);

            resp.getWriter().print(actualString);
        } catch(InvocationTargetException e) {
            resp.sendError(500, "exception when calling method someThrowingMethod : "+e.getTargetException().getMessage());
        } catch(Exception e) {
            resp.sendError(500, "exception when calling method someThrowingMethod : "+e.getMessage());
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // on enregistre notre controller au démarrage de la servlet
        this.registerController(HelloController.class);
        this.registerController(PokemonTypeController.class);
    }

    protected void registerController(Class controllerClass){
        System.out.println("Analysing class " + controllerClass.getName());
        if(controllerClass.isAnnotationPresent(Controller.class)) {
            for(Method m : controllerClass.getDeclaredMethods()) {
                if(m.isAnnotationPresent(RequestMapping.class)) {
                    this.registerMethod(m);
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected void registerMethod(Method method) {
        System.out.println("Registering method " + method.getName());
        if(method.getReturnType() != void.class){
            uriMappings.put(method.getAnnotation(RequestMapping.class).uri(), method);
        }
    }

    protected Map<String, Method> getMappings(){
        return this.uriMappings;
    }

    protected Method getMappingForUri(String uri){
        return this.uriMappings.get(uri);
    }
}