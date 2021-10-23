package com.mobileshop.group8.controller;


import com.mobileshop.group8.common.Constants;
import com.mobileshop.group8.model.Member;
import com.mobileshop.group8.model.Product;
import com.mobileshop.group8.model.cart.Cart;
import com.mobileshop.group8.model.cart.CartBean;
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
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
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

    @RequestMapping(value = "/addtocart", params = {"id"}, method = RequestMethod.GET)
    public ModelAndView addToCart(@RequestParam("id") Integer productId, HttpSession session) {
        CartBean carlist = (CartBean) session.getAttribute(Constants.ATTR_CART);
        if (carlist == null){
            carlist = new CartBean();
        }
        Product product = this.productService.findByProductId(productId);
        if (product != null) {
            Cart cart = new Cart(product,1);
            carlist.addProduct(cart);
            session.setAttribute(Constants.ATTR_CART,carlist);
            return new ModelAndView(Constants.VIEW_CART_URL);
        }
        return new ModelAndView(Constants.HOME_URL);

    }

    @RequestMapping(value = "/removecart", method = RequestMethod.GET)
    public RedirectView removeCart(@RequestParam("id") Integer productId, HttpSession session) {
        CartBean carlist = (CartBean) session.getAttribute(Constants.ATTR_CART);
        if (carlist != null){
            Product product = this.productService.findByProductId(productId);
            if (product != null) {
                carlist.removeProduct(productId);
                session.setAttribute(Constants.ATTR_CART,carlist);
            }
        }
        return new RedirectView("cart");

    }


    @RequestMapping(value ="/cart")
    public ModelAndView viewCart(HttpSession session){
        return new ModelAndView(Constants.VIEW_CART_URL);
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
