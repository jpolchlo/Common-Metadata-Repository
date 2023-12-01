(ns cmr.search.site.pages
  "The functions of this namespace are specifically responsible for returning
  ready-to-serve pages."
  (:require
   [cmr.common-app.site.pages :as common-pages]
   [cmr.search.site.data :as data]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; HTML page-genereating functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn home
  "Prepare the home page template."
  [context]
  (common-pages/render-html
   context
   "templates/search-home.html"
   (data/base-page context)))

(defn search-docs
  "Prepare the top-level search docs page."
  [context]
  (common-pages/render-html
   context
   "templates/search-docs.html"
   (data/base-page context)))

(defn stac-search-docs
  "Prepare the top-level stac search docs page."
  [context]
  (common-pages/render-html
   context
   "templates/stac-search-docs.html"
   (data/base-page context)))

(defn search-site-docs
  "Prepare the site-specific (non-API) docs page."
  [context]
  (common-pages/render-html
   context
   "templates/search-site-docs.html"
   (data/base-page context)))

(defn collections-directory
  "Prepare the page that links to all sub-directory pages.

  For now, this is just a page with a single link (the EOSDIS collections
  directory)."
  [context]
  (common-pages/render-html
   context
   "templates/search-directory-links.html"
   (data/get-directory-links context)))

(defn eosdis-collections-directory
  "Prepare the EOSDIS directory page that provides links to all the providers."
  [context]
  (common-pages/render-html
   context
   "templates/search-eosdis-directory-links.html"
   (data/get-eosdis-directory-links context)))

(defn provider-tag-directory
  "Prepare the page that provides links to collection landing pages based
  upon a provider and a tag."
  [context provider-id tag]
  (common-pages/render-html
   context
   "templates/search-provider-tag-landing-links.html"
   (data/get-provider-tag-landing-links context provider-id tag)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; XML resource-genereating functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn sitemap-master
  "Prepare the XML page that provides the master sitemap, which collects all
  CMR sitemaps together."
  [context]
  (common-pages/render-xml
   context
   "templates/search-sitemap-master.xml"
   (data/get-eosdis-directory-links context)))

(defn sitemap-top-level
  "Prepare the XML page that provides the top-most sitemap."
  [context]
  (common-pages/render-xml
   context
   "templates/search-sitemap-top-level.xml"
   (data/get-eosdis-directory-links context)))

(defn sitemap-provider-tag
  "Prepare the XML page that provides the sitemap associated with the given
  provider and tag."
  [context provider-id tag]
  (common-pages/render-xml
   context
   "templates/search-sitemap-provider-tag.xml"
   (data/get-provider-tag-sitemap-landing-links context provider-id tag)))

(defn collection-page
  "Prepare the collection landing page."
  [context concept-id]
  (common-pages/render-html
   context
   "templates/collection-base.html"
   (data/get-concept context concept-id)))

(defn service-page
  "Prepare the service landing page."
  [context concept-id]
  (common-pages/render-html
   context
   "templates/service-base.html"
   (data/get-concept context concept-id)))

(defn tool-page
  "Prepare the tool landing page."
  [context concept-id]
  (common-pages/render-html
   context
   "templates/tool-base.html"
   (data/get-concept context concept-id)))

(defn variable-page
  "Prepare the variable landing page."
  [context concept-id]
  (common-pages/render-html
   context
   "templates/variable-base.html"
   (data/get-concept context concept-id)))

(defn csw-retirement
  "Prepare the CSW not found page."
  [context]
  (common-pages/render-html
   context
   "templates/csw-retirement.html"
   (data/csw-page context)))
