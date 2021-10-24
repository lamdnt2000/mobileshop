package com.mobileshop.group8.controller;


import com.mobileshop.group8.common.Constants;
import com.mobileshop.group8.model.Category;
import com.mobileshop.group8.model.Member;
import com.mobileshop.group8.model.Product;
import com.mobileshop.group8.model.cart.Cart;
import com.mobileshop.group8.model.cart.CartBean;
import com.mobileshop.group8.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Autowired
    private CategoryService categoryService;
    public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/image";


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
    @RequestMapping(value="/product", method= RequestMethod.GET)
    public String initPage(Model model){
        List<Category> categories = categoryService.findAll();
        model.addAttribute("product",new Product());
        model.addAttribute("categories",categories);

        return "addproduct";
    }
    @RequestMapping(value="/product", method = RequestMethod.POST)
    public String saveProduct(@RequestParam("productImage") MultipartFile file,
                              @ModelAttribute("productDTO")Product productDTO,
                              @RequestParam("image")String image,
                              @RequestParam("productName")String name,
                              @RequestParam("description")String description,
                              @RequestParam("categoryByCategoryId")String category,
                              @RequestParam("manufacturer")String manufacturer,
                              @RequestParam("condition")String condition,
                              @RequestParam("price")float price,
                              @RequestParam("quantity")int quantity)
            throws IOException {
        Product product = new Product();
        String imageUUID;
        if (!file.isEmpty()) {
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = image;
        }
        product.setImage(imageUUID);
        product.setProductName(name);
        product.setQuantity(quantity);
        product.setPrice(price);
        product.setDescription(description);
        product.setCategoryByCategoryId(categoryService.findById(category));
        product.setCondition(condition);
        product.setManufacturer(manufacturer);
        productService.save(product);
        return "redirect:/";
    }

}
