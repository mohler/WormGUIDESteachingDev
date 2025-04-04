/*
 * Bao Lab 2017
 */

package application_src.application_model.annotation.color.URL;

import application_src.application_model.annotation.color.Rule;

import java.util.List;


/** Utility methods to generate a URL specifying the subscene color scheme and parameters */
public class UrlGenerator {

    public static String generateIOS(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dimN,
            final double dimC,
            final double dimT,
            final double dimS) {

        return "wormguides://wormguides/testurlscript?"
                + generateParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dimN,
                dimC,
                dimT,
                dimS)
                + "/iOS/";
    }

    public static String generateAndroid(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dimN,
            final double dimC,
            final double dimT,
            final double dimS) {

        return "http://scene.wormguides.org/wormguides/testurlscript?"
                + generateParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dimN,
                dimC,
                dimT,
                dimS)
                + "/Android/";
    }

    public static String generateWeb(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dimN,
            final double dimC,
            final double dimT,
            final double dimS) {

        return "http://scene.wormguides.org/wormguides/testurlscript?"
                + generateParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dimN,
                dimC,
                dimT,
                dimS)
                + "/browser/";
    }

    public static String generateInternal(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dimN,
            final double dimC,
            final double dimT,
            final double dimS) {

        return "http://scene.wormguides.org/wormguides/testurlscript?"
                + generateParameterString(
                rules,
                time,
                rX,
                rY,
                rZ,
                tX,
                tY,
                scale,
                dimN,
                dimC,
                dimT,
                dimS)
                + "/browser/";
    }

    public static String generateInternalWithoutViewArgs(final List<Rule> rules) {
        return "http://scene.wormguides.org/wormguides/testurlscript?"
                + generateSetParameters(rules);
    }

    private static String generateParameterString(
            final List<Rule> rules,
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dimN,
            final double dimC,
            final double dimT,
            final double dimS) {

        return generateSetParameters(rules) + generateViewParameters(time, rX, rY, rZ, tX, tY, scale,
        		dimN,
                dimC,
                dimT,
                dimS);
    }

    private static String generateSetParameters(final List<Rule> rules) {
        final StringBuilder builder = new StringBuilder("/set");

        for (Rule rule : rules) {
            String ruleName = rule.getSearchedText();

            // this is the delimiter for entities in the Manually Specified List
            if (ruleName.contains(";")) {
                ruleName = ruleName.substring(1, ruleName.lastIndexOf("'")); // remove the single quotes
                ruleName = ruleName.replaceAll("\\s", ""); //remove all white space
            } else if (ruleName.contains("'")) {
                ruleName = ruleName.substring(0, ruleName.lastIndexOf("'"));
                ruleName = ruleName.substring(ruleName.indexOf("'") + 1, ruleName.length());
                ruleName = ruleName.replace(" ", "=");
            }
            builder.append("/").append(ruleName);

            // rule from cell search
            // rule from multicellular structure search
            if (rule.isStructureRuleBySceneName()) {
                // specify a multicellular structure rule that is not cell-based, but scene name-based
                builder.append("-M");
            } else if (rule.isStructureRuleByHeading()) {
                builder.append("-H");
            } else {
                // search types
                switch (rule.getSearchType()) {
                    case LINEAGE:
                        builder.append("-s");
                        break;
                    case DESCRIPTION:
                        builder.append("-d");
                        break;
                    case FUNCTIONAL:
                        builder.append("-n");
                        break;
                    case MULTICELLULAR_STRUCTURE_CELLS:
                        builder.append("-m");
                        break;
                    case GENE:
                        builder.append("-g");
                        break;
                    case NEIGHBOR:
                        builder.append("-b");
                        break;
                    case CONNECTOME:
                        builder.append("-c");
                        break;
                    case MSL:
                        builder.append("-MSL");
                    case ALL_RULES_IN_LIST:
                        break;
                    default:
                        break;
                }

                // ancestry modifiers
                // descendant
                if (rule.isAncestorSelected()) {
                    builder.append("<");
                }
                // cell
                if (rule.isCellSelected()) {
                    builder.append("$");
                }
                // cell body
                if (rule.isCellBodySelected()) {
                    builder.append("@");
                }
                // ancestor
                if (rule.isDescendantSelected()) {
                    builder.append(">");
                }
            }

            // color
            // get color in its native javafx format: 0x (prefix) 11 (red) 22 (green) 33 (blue) ff (alpha)
            // 0x112233ff
            final String colorString = rule.getColor().toString();
            // append as url format: #ff112233
            builder.append("+")
                    .append("#")
                    .append(colorString.substring(8))
                    .append(colorString.substring(2, 8));
        }
        return builder.toString();
    }

    private static String generateViewParameters(
            final int time,
            final double rX,
            final double rY,
            final double rZ,
            final double tX,
            final double tY,
            final double scale,
            final double dimN,
            final double dimC,
            final double dimT,
            final double dimS) {

        return "/view"
                + "/time=" + time
                + "/rX=" + rX
                + "/rY=" + rY
                + "/rZ=" + rZ
                + "/tX=" + tX
                + "/tY=" + tY +
                "/scale=" + scale
                + "/dimN=" + dimN
                + "/dimC=" + dimC
                + "/dimT=" + dimT
                + "/dimS=" + dimS;
    }
}
