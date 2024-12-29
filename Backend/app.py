from flask import Flask, request, jsonify
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from bs4 import BeautifulSoup

app = Flask(__name__)  # Correction de _name_ en __name__


def create_driver():
    """
    Crée et retourne une instance de WebDriver en mode headless.
    """
    options = webdriver.ChromeOptions()
    options.add_argument("--headless")
    driver = webdriver.Chrome(options=options)
    return driver


def close_driver(driver):
    """
    Ferme proprement le WebDriver.
    """
    driver.quit()


def scrape_health_section(driver, url):
    """
    Scrapes the health section to extract Nutri-Score, negative, and positive points with associated details.
    """
    try:
        driver.get(url)
        WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "health")))
        soup = BeautifulSoup(driver.page_source, 'html.parser')
        health_section = soup.find('section', {'id': 'health'})

        if not health_section:
            return {"error": "Section 'health' not found"}

        result = {
            "nutriscore": {},
            "negative_points": {},
            "positive_points": {}
        }

        # Nouvelle méthode plus robuste pour extraire le Nutri-Score
        nutriscore_panel = soup.find('ul', {'id': 'panel_nutriscore_2023'})
        if nutriscore_panel:
            # Recherche du grade dans le title
            grade_link = nutriscore_panel.find('a', {'class': lambda x: x and 'grade_' in x})
            if grade_link:
                grade_title = grade_link.find('h4', {'class': lambda x: x and 'grade_' in x})
                if grade_title:
                    result['nutriscore']['grade'] = grade_title.text.strip()

                # Recherche de la qualité nutritionnelle
                quality_span = grade_link.find('span')
                if quality_span:
                    result['nutriscore']['quality'] = quality_span.text.strip()
        else:
            # Fallback sur l'ancienne méthode si le nouveau panel n'est pas trouvé
            grade_title = health_section.find('h4', class_=lambda x: x and 'grade_' in x and '_title' in x)
            if grade_title:
                result['nutriscore']['grade'] = grade_title.text.strip()
                quality_span = grade_title.find_next_sibling('span')
                if quality_span:
                    result['nutriscore']['quality'] = quality_span.text.strip()

        # Extract negative points
        bad_title = health_section.find('h3', {'class': 'evaluation_bad_title'})
        if bad_title:
            result['negative_points']['title'] = bad_title.text.strip()
            result['negative_points']['components'] = []
            for accordion in bad_title.find_next_siblings('ul', {'class': 'panel_accordion'}):
                if accordion.find_previous('h3', {'class': 'evaluation_bad_title'}) == bad_title:
                    link = accordion.find('a', {'class': 'panel_title'})
                    if link:
                        h4 = link.find('h4')
                        span = link.find('span')
                        if h4 and span:
                            component = {
                                "name": h4.text.strip(),
                                "value": span.text.strip()
                            }
                            result['negative_points']['components'].append(component)

        # Extract positive points
        good_title = health_section.find('h3', {'class': 'evaluation_good_title'})
        if good_title:
            result['positive_points']['title'] = good_title.text.strip()
            result['positive_points']['components'] = []
            for accordion in good_title.find_next_siblings('ul', {'class': 'panel_accordion'}):
                if accordion.find_previous('h3', {'class': 'evaluation_good_title'}) == good_title:
                    link = accordion.find('a', {'class': 'panel_title'})
                    if link:
                        h4 = link.find('h4')
                        span = link.find('span')
                        if h4 and span:
                            component = {
                                "name": h4.text.strip(),
                                "value": span.text.strip()
                            }
                            result['positive_points']['components'].append(component)

        return result
    except Exception as e:
        return {"error": f"An error occurred: {str(e)}"}


def get_product_details_selenium(driver, product_code):
    """
    Récupère les informations spécifiques d'un produit avec WebDriverWait, y compris les ingrédients.
    """
    url = f"https://fr.openfoodfacts.org/produit/{product_code}"
    try:
        driver.get(url)
        wait = WebDriverWait(driver, 10)

        # Récupérer le nom du produit
        product_name = wait.until(
            EC.presence_of_element_located((By.CSS_SELECTOR, ".main-product h1[property='food:name']"))
        ).text.strip()

        # Récupérer les champs principaux
        quantity = wait.until(EC.presence_of_element_located((By.ID, "field_quantity_value"))).text.strip()
        brands = wait.until(EC.presence_of_element_located((By.ID, "field_brands_value"))).text.strip()
        categories = wait.until(EC.presence_of_element_located((By.ID, "field_categories_value"))).text.strip()

        # Ingrédients
        ingredients_element = wait.until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "#panel_ingredients_content .panel_text"))
        )
        ingredients = ingredients_element.text.strip()

        return {
            "product_name": product_name,  # Add product name to the response
            "quantity": quantity,
            "brands": brands,
            "categories": categories,
            "ingredients": ingredients,
        }
    except Exception as e:
        return {"error": f"Erreur lors de la récupération des détails: {str(e)}"}


def get_environmental_impact_selenium(driver, product_code):
    """
    Récupère les informations environnementales d'un produit.
    """
    url = f"https://fr.openfoodfacts.org/produit/{product_code}"
    try:
        driver.get(url)
        soup = BeautifulSoup(driver.page_source, 'html.parser')
        env_section = soup.find('section', {'id': 'environment'})

        if not env_section:
            return {"error": "Section environnement non trouvée"}

        result = {"green_score": "Non trouvé", "li_texts": [], "impacts_by_stage": {}}

        # Score environnemental
        for grade in ['a_plus', 'a', 'b', 'c', 'd', 'e']:
            score_title = env_section.find('h4', class_=f'grade_{grade}_title')
            if score_title:
                result["green_score"] = score_title.text.strip()
             

        # Détails Agribalyse
        agribalyse_section = env_section.find('div', {'id': 'panel_environmental_score_agribalyse_content'})
        if agribalyse_section:
            panel_text = agribalyse_section.find('div', class_='panel_text')
            if panel_text:
                result["li_texts"] = [li.text.strip() for li in panel_text.find_all('li')]

            table = agribalyse_section.find('table')
            if table:
                for row in table.find_all('tr')[1:]:
                    cols = row.find_all('td')
                    if len(cols) >= 2:
                        stage_name = cols[0].find('span', class_=None)
                        if stage_name:
                            result["impacts_by_stage"][cols[0].text.strip()] = cols[1].text.strip()

        return result
    except Exception as e:
        return {"error": f"Erreur lors de la récupération des impacts: {str(e)}"}


def get_carbon_footprint_selenium(driver, product_code):
    """
    Récupère les informations de l'empreinte carbone d'un produit avec une meilleure gestion des sélecteurs.
    """
    url = f"https://fr.openfoodfacts.org/produit/{product_code}"
    try:
        driver.get(url)
        wait = WebDriverWait(driver, 10)

        # Attendre que la section carbone soit chargée
        carbon_section = wait.until(
            EC.presence_of_element_located((By.ID, "panel_carbon_footprint"))
        )

        # Utilisation de BeautifulSoup pour parser le contenu HTML
        soup = BeautifulSoup(carbon_section.get_attribute('innerHTML'), 'html.parser')

        # Récupérer la description
        description_text = ""
        for class_name in ['evaluation_bad_title', 'evaluation_good_title', 'evaluation_average_title']:
            description_tag = soup.find('h4', class_=class_name)
            if description_tag:
                description_text = description_tag.get_text(strip=True)
                break

        # Récupérer la valeur CO2 - Modification ici
        co2_text = ""
        description_h4 = soup.find('h4')
        if description_h4:
            co2_span = description_h4.find_next_sibling('span')
            if co2_span:
                co2_text = co2_span.get_text(strip=True)

        # Récupérer les impacts par étape
        impacts = {}
        table = soup.find('table')
        if table:
            rows = table.find_all('tr')[1:]  # Skip header
            for row in rows:
                cols = row.find_all('td')
                if len(cols) >= 2:
                    stage_name = cols[0].get_text(strip=True)
                    impact_value = cols[1].get_text(strip=True)
                    impacts[stage_name] = impact_value

        result = {
            'description': description_text,
            'co2': co2_text,
            'impacts_by_stage': impacts
        }

        return result
    except Exception as e:
        print(f"Error in carbon footprint scraping: {str(e)}")
        return {'error': f'Erreur: {str(e)}'}


def get_product_image(driver, product_code):
    """
    Récupère l'URL de l'image du produit depuis OpenFoodFacts (balise meta twitter:image).
    """
    url = f"https://fr.openfoodfacts.org/produit/{product_code}"
    try:
        driver.get(url)
        WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.TAG_NAME, "meta")))
        # Utilisation de BeautifulSoup pour parser la page
        soup = BeautifulSoup(driver.page_source, 'html.parser')
        # Chercher la balise <meta name="twitter:image">
        meta_tag = soup.find('meta', {'name': 'twitter:image'})
        if meta_tag:
            image_url = meta_tag.get('content')
            return {"image_url": image_url}
        else:
            return {"error": "Image non trouvée"}
    except Exception as e:
        return {"error": f"Erreur: {str(e)}"}


@app.route('/api/product', methods=['GET', 'POST'])
def get_product_info():
    """
    Endpoint pour récupérer les informations d'un produit.
    Accepte les requêtes GET et POST avec un code-barres.
    """
    product_code = None
    if request.method == 'GET':
        product_code = request.args.get('barcode')
    elif request.method == 'POST':
        data = request.get_json()
        if data:
            product_code = data.get('barcode')

    if not product_code:
        return jsonify({"error": "Code-barres non fourni"}), 400

    try:
        driver = create_driver()
        try:
            # Construire l'URL une seule fois
            url = f"https://fr.openfoodfacts.org/produit/{product_code}"

            # Récupérer toutes les informations
            product_details = get_product_details_selenium(driver, product_code)
            environmental_impact = get_environmental_impact_selenium(driver, product_code)
            carbon_footprint = get_carbon_footprint_selenium(driver, product_code)
            health_info = scrape_health_section(driver, url)
            product_image = get_product_image(driver, product_code)  # Ajout de l'image

            response = {
                "product_details": product_details,
                "environmental_impact": environmental_impact,
                "carbon_footprint": carbon_footprint,
                "health_info": health_info,
                "product_image": product_image  # Ajout dans la réponse
            }

            if any("error" in res for res in
                   [product_details, environmental_impact, carbon_footprint, health_info, product_image]):
                return jsonify(response), 500

            return jsonify(response), 200
        finally:
            close_driver(driver)
    except Exception as e:
        return jsonify({"error": f"Erreur inattendue: {str(e)}"}), 500


if __name__ == '__main__':
    app.run(debug=True)