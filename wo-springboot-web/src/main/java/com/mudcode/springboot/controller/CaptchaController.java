package com.mudcode.springboot.controller;

import com.google.code.kaptcha.Producer;
import com.mudcode.springboot.Constant;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class CaptchaController {

    @Autowired
    private Producer captchaProducer;

    @GetMapping(value = "/captcha.jpg")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set to expire far in the past.
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");

        // return a jpeg
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        // create the text for the image
        String captchaText = captchaProducer.createText();

        request.getSession().setAttribute(Constant.CAPTCHA_SESSION_KEY, captchaText);

        BufferedImage bi = captchaProducer.createImage(captchaText);
        try (ServletOutputStream out = response.getOutputStream()) {
            ImageIO.write(bi, "jpeg", out);
            out.flush();
        }
    }

}
