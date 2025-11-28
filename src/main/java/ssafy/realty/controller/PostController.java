package ssafy.realty.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ssafy.realty.Entity.Post;
import ssafy.realty.Mapper.PostMapper;

@Controller
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class PostController {

    private final PostMapper postMapper;

}
