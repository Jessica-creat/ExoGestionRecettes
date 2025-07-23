package com.example.gestionrecettes.controller;

import com.example.gestionrecettes.model.Recette;
import com.example.gestionrecettes.model.Categorie;
import com.example.gestionrecettes.repository.RecetteRepository;
import com.example.gestionrecettes.repository.CategorieRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /*@GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Recette recette = recetteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recette introuvable : " + id));

        model.addAttribute("recette", recette);
        model.addAttribute("allCategories", categorieRepository.findAll()); // ← AJOUT CRITIQUE

        return "recettes/edit";
    }*/

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        try {
            Recette recette = recetteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Recette introuvable"));

            // Log critique pour débogage
            System.out.println("[DEBUG] Recette à éditer : " + recette.getNom());
            System.out.println("[DEBUG] Catégorie associée : " + recette.getCategorie().getNom());
            System.out.println("[DEBUG] Nombre de catégories disponibles : " +
                    categorieRepository.count());

            model.addAttribute("recette", recette);
            model.addAttribute("allCategories", categorieRepository.findAll());

            return "recettes/edit";
        } catch (Exception e) {
            System.err.println("[ERROR] Échec du chargement de l'édition : " + e.getMessage());
            throw e; // Pour voir la stacktrace complète dans les logs
        }
    }

    @GetMapping("/test/{id}")
    @ResponseBody
    public ResponseEntity<String> testRecette(@PathVariable Long id) {
        try {
            // 1. Récupération de la recette
            Recette recette = recetteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Recette non trouvée avec l'ID : " + id));

            // 2. Logs pour débogage (à vérifier dans la console ou les logs serveur)
            System.out.println("[DEBUG] Recette trouvée : " + recette.getNom());
            System.out.println("[DEBUG] Catégorie associée : " +
                    (recette.getCategorie() != null ? recette.getCategorie().getNom() : "null"));

            // 3. Réponse structurée
            String response = String.format(
                    "Test réussie - Recette [ID=%d, Nom=%s, Catégorie=%s]",
                    recette.getId(),
                    recette.getNom(),
                    recette.getCategorie() != null ? recette.getCategorie().getNom() : "Aucune"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 4. Gestion des erreurs détaillée
            String errorMessage = "Échec du test pour l'ID " + id + " : " + e.getMessage();
            System.err.println("[ERROR] " + errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
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