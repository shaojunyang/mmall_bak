package com.mmall.controller.portal;

import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * a
 *
 * @author yangshaojun
 * @create 2017-12-07 下午10:22
 **/

@Controller()
@RequestMapping("/test")
public class TestController {
    private Logger logger= LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/test.do")
    public void test(HttpServletResponse response) throws IOException {
        logger.info("info111");
        logger.info("succes1111s");
    }
}
