package com.example.crud.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.crud.entity.Product;
import com.example.crud.repository.ProductRepository;

@Controller
public class HomeController {
    

    @Autowired
    private ProductRepository productRepo;

    @GetMapping("/")
    public String home(Model m)
    {
        /*List<Product> list =productRepo.findAll();
        m.addAttribute("all_products", list);*/
        
        return findPaginationAndSorting(0, "id", "asc", m);
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginationAndSorting(@PathVariable(value = "pageNo") int pageNo, 
    @RequestParam("sortField") String sortField,
    @RequestParam("sortDir") String sortDir,
    Model m)
    {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo, 9,sort);

        Page<Product> page = productRepo.findAll(pageable);

        List<Product> list=page.getContent();

        m.addAttribute("pageNo", pageNo);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPage", page.getTotalPages());
        m.addAttribute("all_products", list);

        m.addAttribute("sortField", sortField);
        m.addAttribute("sortDir", sortDir);
        m.addAttribute("revSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "index";
    }
    
    @GetMapping("/load_form")
    public String loadForm()
    {
        return "add";
    }

    
    @GetMapping("/edit_form/{id}")
    public String editForm(@PathVariable(value = "id") long id, Model m)
    {
        Optional<Product> prod = productRepo.findById(id);
        Product prodt = prod.get();
        m.addAttribute("product", prodt);
        return "edit";
    }

    @PostMapping("/save_products")
    public String saveProducts(@ModelAttribute Product prod, HttpSession session)
    {

        productRepo.save(prod);
        session.setAttribute("msg", "Product Added Successfully");

        return "redirect:/load_form";
    }
    

    @PostMapping("/edit_products")
    public String editProducts(@ModelAttribute Product prod, HttpSession session)
    {

        productRepo.save(prod);
        session.setAttribute("msg", "Product Edited Successfully");

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteProducts(@PathVariable(value = "id") long id, HttpSession session)
    {
        productRepo.deleteById(id);
        session.setAttribute("msg", "Product Deleted Successfully");
        return "redirect:/";
    }
}
