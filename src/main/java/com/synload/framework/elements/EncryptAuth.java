package com.synload.framework.elements;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.net.util.Base64;
import nl.captcha.Captcha;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;

public class EncryptAuth extends Response {
    public EncryptAuth(Captcha c) throws IOException {
        this.setTemplateId("encKey");
        this.setTemplate(this.getTemplate("./elements/encrypt.html"));
        this.setAction("alone");
        this.setPageId("encryptBox");
        this.setParent("#body");
        this.setParentTemplate("full");
        Map<String, String> tmp = new HashMap<String, String>();
        BufferedImage img = c.getImage();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(img, "png", os);
        tmp.put("image", StringUtils.newString(
                Base64.encodeBase64(os.toByteArray()), "UTF-8"));
        this.setData(tmp);
        this.setRequest(new Request("get", "encryptauth"));
        this.setPageTitle(" .::. Encryption Key Required");
    }
}