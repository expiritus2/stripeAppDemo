package com.stripeappdemo.controllers;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripeappdemo.models.CartItem;
import com.stripeappdemo.models.Product;
import com.stripeappdemo.repository.CartItemRepository;
import com.stripeappdemo.repository.ProductRepository;
import com.stripeappdemo.repository.ShoppingCartRepository;
import com.stripeappdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stripe")
public class StripeController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String stripe(Model model) {
        List<Product> productList = (List<Product>) productRepository.findAll();
        List<CartItem> cartItemList = (List<CartItem>) cartItemRepository.findAll();

        BigDecimal total = new BigDecimal(0);

        for (CartItem item : cartItemList) {
            total = total.add(item.getSubTotal());
        }

        model.addAttribute("productList", productList);
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("total", total.abs());

        return "stripe-cart";
    }

    @RequestMapping("/addToCart")
    public String addToCart(@RequestParam("id") Long id) {
        Product product = productRepository.findOne(id);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQty(1);
        cartItem.setSubTotal(new BigDecimal(product.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        cartItemRepository.save(cartItem);

        return "redirect:/stripe/";
    }

    @RequestMapping("/remove")
    public String remove(@RequestParam("id") Long id) {
        cartItemRepository.delete(id);

        return "redirect:/stripe/";
    }

    @RequestMapping(value = "/updateCart", method = RequestMethod.POST)
    public String updateCart(HttpServletRequest request) {
        Long id = Long.parseLong(request.getParameter("id"));
        int qty = Integer.parseInt(request.getParameter("qty"));

        CartItem cartItem = cartItemRepository.findOne(id);
        cartItem.setQty(qty);
        cartItem.setSubTotal(new BigDecimal(cartItem.getProduct().getPrice() * qty).setScale(2, BigDecimal.ROUND_HALF_UP));
        cartItemRepository.save(cartItem);

        return "redirect:/stripe/";
    }

    @RequestMapping(value = "/checkoutPay", method = RequestMethod.POST)
    public String checkoutPay(HttpServletRequest request, Model model) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Stripe.apiKey = "sk_test_WmBy5orMamktgk8Ole9RBmS4";

// Token is created using Stripe.js or Checkout!
// Get the payment token submitted by the form:
        String token = request.getParameter("stripeToken");

// Charge the user's card:
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", 1000);
        params.put("currency", "usd");
        params.put("description", "Example charge");
        params.put("source", token);

        Charge charge = Charge.create(params);

        model.addAttribute("checkoutPaySuccess", true);

        return "forward:/stripe/";
    }

    @RequestMapping(value = "/elementsPay", method = RequestMethod.POST)
    public String elementsPay(HttpServletRequest request, Model model) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
// Set your secret key: remember to change this to your live secret key in production
// See your keys here: https://dashboard.stripe.com/account/apikeys
        Stripe.apiKey = "secret_key";

// Token is created using Stripe.js or Checkout!
// Get the payment token submitted by the form:
        String token = request.getParameter("stripeToken");

// Create a Customer:
        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("email", "testcustomer@example.com");
        customerParams.put("source", token);
        Customer customer = Customer.create(customerParams);

// Charge the Customer instead of the card:
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", 1000);
        chargeParams.put("currency", "usd");
        chargeParams.put("customer", customer.getId());
        Charge charge = Charge.create(chargeParams);

// YOUR CODE: Save the customer ID and other info in a database for later.

// YOUR CODE (LATER): When it's time to charge the customer again, retrieve the customer ID.
//        Map<String, Object> chargeParams = new HashMap<String, Object>();
//        chargeParams.put("amount", 1500); // $15.00 this time
//        chargeParams.put("currency", "usd");
//        chargeParams.put("customer", customerId);
//        Charge charge = Charge.create(chargeParams);

        model.addAttribute("elementsPaySuccess", true);
        return "forward:/stripe/";
    }

}
