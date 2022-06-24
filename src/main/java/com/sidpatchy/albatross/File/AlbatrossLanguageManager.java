package com.sidpatchy.albatross.File;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AlbatrossLanguageManager {
    private final String fallbackLocaleString;
    private final File pathToLangFilesFromPluginDataDirectory;
    private final JavaPlugin plugin;

    /**
     * Creates a new AlbatrossLanguageManager
     *
     * @param fallbackLocaleString path to the language file to use if a suitable file isn't located for the selected locale.
     * @param plugin plugin utilizing the language manager
     */
    public AlbatrossLanguageManager(String fallbackLocaleString, JavaPlugin plugin) {
        this.fallbackLocaleString = fallbackLocaleString;
        this.pathToLangFilesFromPluginDataDirectory = plugin.getDataFolder();
        this.plugin = plugin;
    }

    /**
     * Creates a new AlbatrossLanguageManager
     *
     * @param fallbackLocaleString path to the language file to use if a suitable file isn't located for the selected locale.
     * @param pathToLangFilesFromPluginDataDirectory path to the directory that contains language files.
     * @param plugin plugin utilizing the language manager
     */
    public AlbatrossLanguageManager(String fallbackLocaleString, String pathToLangFilesFromPluginDataDirectory, JavaPlugin plugin) {
        this.fallbackLocaleString = fallbackLocaleString;
        this.pathToLangFilesFromPluginDataDirectory = new File(plugin.getDataFolder(), pathToLangFilesFromPluginDataDirectory);
        this.plugin = plugin;
    }


    public String getLocalizedString(String key, Player player) throws IOException, InvalidConfigurationException {
        String locale = player.getLocale();
        AlbatrossConfiguration languageFile = getLangFileByLocale(getTwoLetterLanguageCodeFromMinecraftLocaleString(locale));
        languageFile.loadConfiguration();

        String localizedString;
        try {
            localizedString = languageFile.getString(key);
        }
        /*
            This shouldn't ever happen. It means that the yaml file doesn't have a parameter matching the key provided.

            This could be caused by any of the following issues:
            1) The plugin is searching for a key that isn't intended to exist.
            2) The language files are outdated and lack updated keys.
            3) The localization was performed incorrectly and for some reason resulted in the deletion of all
               untranslated keys from the yaml file. It is best practice to leave untranslated strings in the language
               file rather than delete them.
         */
        catch (Exception ignored) {
            languageFile = new AlbatrossConfiguration("lang-" + fallbackLocaleString + ".yml", plugin);
            try {
                localizedString = languageFile.getString(key);
            }
            /*
                If this happens #1 and/or #2 are likely to be the case.
             */
            catch (Exception e){
                plugin.getLogger().severe("Unable to locate key \"" + key + "\" in fallback or localized language file.");
                localizedString = "There was an unrecoverable error while reading from the language file";
            }
        }
        return localizedString;
    }

    /**
     * Returns a file based off the language string specified.
     * Returns the fallback file if a suitable translation isn't found.
     *
     * @param localeString an ISO 639-3 locale string.
     * @return Returns a localized language file or the fallback file if a suitable translation doesn't exist.
     */
    private AlbatrossConfiguration getLangFileByLocale(String localeString) {
        File file = new File(plugin.getDataFolder(), "lang-" + localeString + ".yml");

        if (file.exists()) {
            return new AlbatrossConfiguration("lang-" + localeString + ".yml", plugin);
        }
        else {
            return new AlbatrossConfiguration("lang-" + fallbackLocaleString + ".yml", plugin);
        }
    }

    /**
     * Converts Minecraft's locale string to an ISO 639-3 language code.
     *
     * This took a long time, regardless, there are probably a considerable number of errors. If you find that I have
     * made one, please create an issue or open a pull request.
     *
     * Includes languages found in Minecraft 1.19 and onward. If you desire a language that was removed before Minecraft
     * 1.19, please open an issue or pull request.
     *
     * Andalusian and valencian intentionally select the Spanish translation as they do not have an ISO 639-3 code.
     * If at some point in time this changes, please create an issue or open a pull request.
     *
     * "Joke" languages select the language they are based off. For example, LOLCAT selects English.
     *
     * Anglish selects english as it doesn't have an ISO 639-3 code. If this changes, open an issue or pull request.
     *
     * Interslavic selects Church Slavic ("chu") as according to 2 seconds on Wikipedia they're pretty similar. If this
     * assumption is wrong, open an issue or pull request.
     *
     * @param minecraftLocaleString the locale string reported by Minecraft.
     * @return Returns an ISO 639-3 language code.
     */
    private String getTwoLetterLanguageCodeFromMinecraftLocaleString(String minecraftLocaleString) {
        HashMap<String, String> localeList = new HashMap<>() {{
            put("af_za", "afr");
            put("ar_sa", "ara");
            put("ast_es", "ast");
            put("az_az", "aze");
            put("ba_ru", "bak");
            put("bar", "bar");
            put("be_by", "bel");
            put("bg_bg", "bul");
            put("br_fr", "bre");
            put("brb", "qbr");
            put("bs_ba", "bos");
            put("ca_es", "cat");
            put("cs_cz", "ces");
            put("cy_gb", "cym");
            put("da_dk", "dan");
            put("de_at", "bar");
            put("de_ch", "gsw");
            put("de_de", "deu");
            put("el_gr", "ell");
            put("en_au", "eng");
            put("en_ca", "eng");
            put("en_gb", "eng");
            put("en_nz", "eng");
            put("en_pt", "eng");
            put("en_ud", "eng");
            put("en_us", "eng");
            put("enp", "eng");
            put("enws", "eng");
            put("eo_uy", "epo");
            put("es_ar", "spa");
            put("es_cl", "spa");
            put("es_ec", "spa");
            put("es_es", "spa");
            put("es_mx", "spa");
            put("es_uy", "spa");
            put("es_ve", "spa");
            put("esan", "spa");
            put("et_ee", "ets");
            put("eu_es", "eus");
            put("fa_ir", "fa");
            put("fi_fi", "fin");
            put("fil_ph", "fil");
            put("fo_fo", "fao");
            put("fr_ca", "fra");
            put("fr_fr", "fra");
            put("fra_de", "vmf");
            put("fur_it", "fur");
            put("fy_nl", "fur");
            put("ga_ie", "gle");
            put("gd_gb", "gla");
            put("gl_es", "glg");
            put("haw_us", "haw");
            put("he_il", "heb");
            put("hi_in", "hin");
            put("hr_hr", "hrv");
            put("hu_hu", "hun");
            put("hy_am", "hye");
            put("id_id", "ind");
            put("ig_ng", "ibo");
            put("io_en", "ido");
            put("is_is", "isl");
            put("isv", "qis");
            put("it_it", "ita");
            put("ja_jp", "jpn");
            put("jbo_en", "jbo");
            put("ka_ge", "kat");
            put("kk_kz", "kaz");
            put("kn_in", "kan");
            put("ko_kr", "kor");
            put("ksh", "ksh");
            put("kw_gb", "cor");
            put("la_la", "lat");
            put("lb_lu", "ltz");
            put("li_li", "lim");
            put("lmo", "lmo");
            put("lol_us", "eng");
            put("lt_lt", "lit");
            put("lv_lv", "lav");
            put("lzh", "lzh");
            put("mk_mk", "mkd");
            put("mn_mn", "mon");
            put("ms_my", "zlm");
            put("mt_mt", "mlt");
            put("nds_de", "nds");
            put("nl_be", "nld");
            put("nl_nl", "nld");
            put("nn_no", "nno");
            put("no_no", "nob");
            put("oc_fr", "oci");
            put("ovd", "ovd");
            put("pl_pl", "pol");
            put("pt_br", "por");
            put("pt_pt", "por");
            put("qya_aa", "qya");
            put("ro_ro", "ron");
            put("rpr", "rus");
            put("ru_ru", "rus");
            put("se_no", "sme");
            put("sk_sk", "slk");
            put("sl_si", "slv");
            put("so_so", "som");
            put("sq_al", "sqi");
            put("sr_sp", "srp");
            put("sv_se", "swe");
            put("sxu", "sxu");
            put("szl", "szl");
            put("ta_in", "tam");
            put("th_th", "tha");
            put("tl_ph", "tgl");
            put("tlh_aa", "tlh");
            put("tok", "tok");
            put("tr_tr", "tur");
            put("tt_ru", "tat");
            put("uk_ua", "ukr");
            put("val_es", "spa");
            put("vec_it", "vec");
            put("vi_vn", "vie");
            put("yi_de", "yid");
            put("yo_ng", "yor");
            put("zh_cn", "zho");
            put("zh_hk", "zho");
            put("zh_tw", "zho");
            put("zlm_arab", "zim");
        }};

        return localeList.get(minecraftLocaleString);
    }
}
