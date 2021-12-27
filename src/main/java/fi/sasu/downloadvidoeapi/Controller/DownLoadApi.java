package fi.sasu.downloadvidoeapi.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DownLoadApi {

    @PostMapping("/download")
    public String downloadApi(@RequestBody Byte input){
        input.toString();
        return "success";
    }
}
