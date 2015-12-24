package stink5.oauth2.lab2.controller;

import static java.util.Arrays.*;
import static java.util.Objects.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import stink5.oauth2.lab2.model.api.Brand;
import stink5.oauth2.lab2.model.api.Model;
import stink5.oauth2.lab2.repository.api.BrandRepository;
import stink5.oauth2.lab2.repository.api.ModelRepository;

@Controller
@RequestMapping("/api")
public class ApiController {

    private final BrandRepository brandRepo;
    private final ModelRepository modelRepo;

    @Autowired
    public ApiController(
        final BrandRepository brandRepo,
        final ModelRepository modelRepo
    ) {
        this.brandRepo = requireNonNull(brandRepo, "brandRepo can't be null");
        this.modelRepo = requireNonNull(modelRepo, "modelRepo can't be null");
    }

    @PostConstruct
    public void initRepoData() {
        if (this.brandRepo.count() == 0) {
            this.brandRepo.save(
                Stream.<Entry<String, List<String>>>of(
                    new SimpleEntry<>("Mercedes", asList("C300", "E350", "S550")),
                    new SimpleEntry<>("Kia", asList("Rio", "Ceed", "Cerato", "Sportage")),
                    new SimpleEntry<>("Hyundai", asList("Solaris", "Accent", "ix35", "SantaFe"))
                ).map(e -> {
                    final Brand b = new Brand();
                    b.setName(e.getKey());

                    b.setModels(e.getValue().stream().map(model -> {
                        final Model m = new Model();
                        m.setName(model);
                        return m;
                    }).collect(Collectors.toSet()));

                    return b;
                }).collect(Collectors.toList())
            );
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/brands")
    public @ResponseBody List<Brand> brands() {
        return this.brandRepo.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/brand/{name}")
    public @ResponseBody Brand brandByName(
        @PathVariable("name") final String brandName
    ) {
        return this.brandRepo.findByName(brandName);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/brand/{brand}/models")
    public @ResponseBody Collection<Model> modelsForBrand(
        @PathVariable("brand") final String brand
    ) {
        return this.brandRepo.findByName(brand).getModels();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/brand/{brand}/model/{name}")
    public @ResponseBody Model modelByName(
        @PathVariable("brand") final String brandName,
        @PathVariable("name") final String modelName
    ) {
        return this.modelRepo.findByBrandAndName(brandName, modelName);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/brand/{brand}/model/{name}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteModelByName(
        @PathVariable("brand") final String brandName,
        @PathVariable("name") final String modelName
        ) {
        final Model model = this.modelRepo.findByBrandAndName(brandName, modelName);
        this.modelRepo.delete(model);
    }

}
