package io.choerodon.agile.infra.feign;

import io.choerodon.agile.infra.config.FeignMultipartSupportConfig;
import io.choerodon.agile.infra.feign.fallback.FileFeignClientFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/3/21.
 * Email: fuqianghuang01@gmail.com
 */
@FeignClient(value = "file-service", fallback = FileFeignClientFallback.class, configuration = FeignMultipartSupportConfig.class)
public interface FileFeignClient {
    @RequestMapping(value = "/v1/files",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadFile(@RequestParam("bucket_name") String bucketName,
                                      @RequestParam("file_name") String fileName,
                                      @RequestPart("file") MultipartFile multipartFile);

    @RequestMapping(value = "/v1/files", method = RequestMethod.DELETE)
    ResponseEntity deleteFile(@RequestParam("bucket_name") String bucketName,
                              @RequestParam("url") String url);
}
