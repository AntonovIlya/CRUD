package servlet;

import controller.PostController;
import repository.PostRepository;
import service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainServlet extends HttpServlet {
    private PostController controller;

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";
    private static final int NUMBER_OF_THREADS = 64;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    @Override
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            if (method.equals(GET) && path.equals("/api/posts")) {
                Runnable muRunnable = () -> {
                    try {
                        controller.all(resp);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                };
                final Future<?> task = threadPool.submit(muRunnable);
                task.get();
                return;
            }
            if (method.equals(GET) && path.matches("/api/posts/\\d+")) {
                final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                Runnable muRunnable = () -> {
                    try {
                        controller.getById(id, resp);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                };
                final Future<?> task = threadPool.submit(muRunnable);
                task.get();
                return;
            }
            if (method.equals(POST) && path.equals("/api/posts")) {
                Runnable muRunnable = () -> {
                    try {
                        controller.save(req.getReader(), resp);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                };
                final Future<?> task = threadPool.submit(muRunnable);
                task.get();
                return;
            }
            if (method.equals(DELETE) && path.matches("/api/posts/\\d+")) {
                final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                Runnable muRunnable = () -> controller.removeById(id, resp);
                final Future<?> task = threadPool.submit(muRunnable);
                task.get();
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
