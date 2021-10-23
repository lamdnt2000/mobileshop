package com.mobileshop.group8.controller;


import com.mobileshop.group8.model.Member;
import com.mobileshop.group8.model.Product;
import com.mobileshop.group8.service.ProductService;
import com.mobileshop.group8.service.SercurityService;
import com.mobileshop.group8.service.UserService;
import com.mobileshop.group8.service.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class BaseController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserValidation userValidation;
    @Autowired
    private UserService userService;
    @Autowired
    private SercurityService sercurityService;



    @RequestMapping(value = "/")
    public ModelAndView index() {
        List<Product> tblProductsList = this.productService.findAll();
        return new ModelAndView("productlist", "products", tblProductsList);
    }

    @RequestMapping(value = "/view", params = {"id"}, method = RequestMethod.GET)
    public ModelAndView searchProduct(@RequestParam("id") Integer productId) {
        Product product = this.productService.findByProductId(productId);
        if (product != null) {
            return new ModelAndView("viewproduct", "product", product);
        }
        return index();

    }

    @PostMapping("/register")
    public String registration(@ModelAttribute("userForm") Member userForm, BindingResult bindingResult) {
        userValidation.validate(userForm,bindingResult);
        if (bindingResult.hasErrors()){
            return "registerpage";
        }
        userService.save(userForm);
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(Model model){
        if (sercurityService.isAuthenticated()){
            return"redirect:/";
        }
        model.addAttribute("userForm",new Member());
        return "registerpage";
    }


    @RequestMapping(value = "/login")
    public String initLogin(Model model, String error, String logout){

        if (sercurityService.isAuthenticated()){
            return "redirect:/";
        }
        if (error!=null){
            model.addAttribute("error", "your username or password is  not validation");
        }
        if (logout !=null){
            model.addAttribute("message","you have been logout successfully");
        }

        return "loginpage";


    }

}
