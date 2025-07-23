package com.example.gestionrecettes.controller;

import com.example.gestionrecettes.model.Recette;
import com.example.gestionrecettes.model.Categorie;
import com.example.gestionrecettes.repository.RecetteRepository;
import com.example.gestionrecettes.repository.CategorieRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/recettes")
public class RecetteController {

    @Autowired
    private RecetteRepository recetteRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @ModelAttribute("allCategories")
    public Iterable<Categorie> populateCategories() {
        return categorieRepository.findAll();
    }

    @GetMapping
    public String listRecettes(Model model) {
        model.addAttribute("recettes", recetteRepository.findAll());
        return "recettes/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("recette", new Recette());
        model.addAttribute("allCategories", categorieRepository.findAll()); // Ajout explicite
        return "recettes/add";
    }

    @PostMapping("/add")
    public String addRecette(@Valid @ModelAttribute("recette") Recette recette,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "recettes/add";
        }

        recetteRepository.save(recette);
        redirectAttributes.addFlashAttribute("successMessage", "Recette ajoutée avec succès");
        return "redirect:/recettes";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Recette recette = recetteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de recette invalide: " + id));
        model.addAttribute("recette", recette);
        model.addAttribute("allCategories", categorieRepository.findAll()); // Ajoutez cette ligne
        return "recettes/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateRecette(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("recette") Recette recette,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "recettes/edit";
        }
        recette.setId(id);
        recetteRepository.save(recette);
        redirectAttributes.addFlashAttribute("successMessage", "Recette mise à jour avec succès");
        return "redirect:/recettes";
    }

    @GetMapping("/delete/{id}")
    public String deleteRecette(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        recetteRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Recette supprimée avec succès");
        return "redirect:/recettes";
    }
}