package com.example.gestionrecettes.controller;

import com.example.gestionrecettes.model.Categorie;
import com.example.gestionrecettes.repository.CategorieRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategorieController {

    @Autowired
    private CategorieRepository categorieRepository;

    @GetMapping
    public String listCategories(Model model) {
        List<Categorie> categories = categorieRepository.findAll();
        model.addAttribute("categories", categories);
        return "categories/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("categorie", new Categorie()); // Initialise l'objet
        return "categories/add";
    }



    @PostMapping("/add")
    public String addCategorie(@Valid @ModelAttribute("categorie") Categorie categorie,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categories/add";
        }
        categorieRepository.save(categorie);
        redirectAttributes.addFlashAttribute("successMessage", "Catégorie ajoutée avec succès");
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de catégorie invalide: " + id));
        model.addAttribute("categorie", categorie);
        return "categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateCategorie(@PathVariable("id") Long id,
                                  @Valid @ModelAttribute("categorie") Categorie categorie,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categories/edit";
        }
        categorie.setId(id);
        categorieRepository.save(categorie);
        redirectAttributes.addFlashAttribute("successMessage", "Catégorie mise à jour avec succès");
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategorie(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes) {
        try {
            Categorie categorie = categorieRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ID de catégorie invalide: " + id));

            // Vérification alternative plus sûre
            if (categorieRepository.isUsedInRecettes(id)) { // Vous devez implémenter cette méthode
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Impossible de supprimer : cette catégorie est utilisée par des recettes");
                return "redirect:/categories";
            }

            categorieRepository.delete(categorie);
            redirectAttributes.addFlashAttribute("successMessage", "Catégorie supprimée avec succès");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Impossible de supprimer : cette catégorie est utilisée par des recettes");
        }
        return "redirect:/categories";
    }
}
