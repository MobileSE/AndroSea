package edu.monash.androsea.equivalentcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.monash.androsea.MethodSig;

public class FunctionallyEquivalentCodeAnalysis
{

    public static final int ModTotal = 12;
	public static final List<String> allMods = new ArrayList<>(Arrays.asList(
			"public",
			"protected",
			"private",
			"default",
			"static",
			"abstract",
			"final",
			"native",
			"hidden",
			"deprecated",
			"internal"));

    public String analyse(Map<String, MethodSig> methodRepo, Map<String, MethodSig> methodRepo2, Set<String> updatedMethods)
    {
        StringBuilder sb = new StringBuilder();

        for (String method : updatedMethods)
        {
            MethodSig ms1 = methodRepo.get(method);
            MethodSig ms2 = methodRepo2.get(method);
            
            StringBuilder modSb = new StringBuilder();

            if (null != ms1 && null != ms2)
            {
                if (ms1.comment.isEmpty() || ms2.comment.isEmpty())
                   continue;
                
                if (!ms1.comment.equals(ms2.comment))
                	continue;
                
                if (ms1.body.equals(ms2.body)) {
                    continue;
                }

                if (!isCoreOfAndroidPlatform(method)) {
                	continue;
                }

//                if (ms1.isPrivate || ms1.isInternal || ms1.isHidden || ms1.isAbstract || ms1.isNative ||
//                	ms2.isPrivate || ms2.isInternal || ms2.isHidden || ms2.isAbstract || ms2.isNative)
//                {
//                	continue;
//                }
                
            	if (isSameMod(ms1, ms2)) {
            		modSb.append("Method Modifier: " + getUnifiedMod(ms1) + "\n");
            	} else {
        	    	String[] modList = getModUpd(ms1, ms2).split("\n");
        	    	String modChange = modList[3] + "->" + modList[4];
        	    	modSb.append(ms1.getSignature() + "\n");
        	    	modSb.append(modChange + "\n");
        	    	modSb.append("Method Modifier: " + modList[0] + "\n");
        	    	modSb.append("Method Modifier: " + modList[1] + "\n");
        	    	modSb.append("Method Updated:  " + modList[2] + "\n");
            	}
                
                if (ms1.comment.equals(ms2.comment))
                {
                    String commentStr = ms1.comment.isEmpty() ? "NO_COMMENT" : "COMMENT";
                    sb.append("Find a silently evolved API code:" + method + ":" + commentStr + "\n");
                    sb.append(modSb.toString());

                    sb.append("Comment:" + ms1.comment + "\n");
                    sb.append("Body of Frist Method:" + "\n");
                    sb.append(ms1.body + "\n");
                    sb.append("Body of Second Method:" + "\n");
                    sb.append(ms2.body + "\n");
                    sb.append("------------------------" + "\n");
                }
            }
        }

        return sb.toString();
    }

    public static boolean isCoreOfAndroidPlatform(String method) {
    	boolean isCore = true;
    	if (method.startsWith("android.")) {
    		if (method.startsWith("android.test.") || method.startsWith("android.support.") 
    				|| method.startsWith("android.arch.") || method.contains("test") || method.contains("Test")) {
    			isCore = false;
    		}
    	} else {
    		if (method.startsWith("java.") || method.startsWith("javax.") || method.startsWith("org.")) {
    			isCore = true;
    		} else {
    			isCore = false;
    		}
    	}
    	
    	return isCore;
    }

    public static boolean isSameMod(MethodSig ms1, MethodSig ms2) {
       	List<String> ms1Mods = getModifiers(ms1);
       	List<String> ms2Mods = getModifiers(ms2);
       	boolean isSame = ms1Mods.equals(ms2Mods);
       	return isSame;
    }

    public static String getUnifiedMod(MethodSig ms) {
       	StringBuilder sbMod = new StringBuilder();
       	List<String> msMods = getModifiers(ms);
       	for (int i = 0; i < msMods.size(); i++) {
       		if (msMods.get(i).equals("Yes")) {
       			String mod = allMods.get(i);
       			sbMod.append(padRightBlank(mod, ModTotal));
       		}
       	}
       	
       	return sbMod.toString();
    }

    public static String getModUpd(MethodSig ms1, MethodSig ms2) {
       	List<String> ms1Mods = getModifiers(ms1);
       	List<String> ms2Mods = getModifiers(ms2);
       	StringBuilder modStr = new StringBuilder();
       	StringBuilder ms1ModStr = new StringBuilder();
       	StringBuilder ms2ModStr = new StringBuilder();
       	StringBuilder modBefore = new StringBuilder();
       	StringBuilder modAfter = new StringBuilder();
       	for (int i = 0; i < ms1Mods.size(); i++) {
       		if (ms1Mods.get(i).equals(ms2Mods.get(i)) && ms1Mods.get(i).equals("No")) {
       			continue;
       		} else {
       			String mod = allMods.get(i);
       			modStr.append(padRightBlank(mod, ModTotal));
       			ms1ModStr.append(padRightBlank(ms1Mods.get(i), ModTotal));
       			ms2ModStr.append(padRightBlank(ms2Mods.get(i), ModTotal));
       			if (!ms1Mods.get(i).equals(ms2Mods.get(i))
       					|| (ms1Mods.get(i).equals(ms2Mods.get(i)) && ms1Mods.get(i).equals("Yes"))) {
   	    			if (ms1Mods.get(i).equals("Yes")) {
   	    				modBefore.append(padRightBlank(mod, ModTotal));
   	    			}/* else {
   	    				modBefore.append(padRightBlank("!" + mod, modLen));
	    			} */
   	    			if (ms2Mods.get(i).equals("Yes")) {
   	    				modAfter.append(padRightBlank(mod, ModTotal));
   	    			}/* else {
   	    				modAfter.append(padRightBlank("!" + mod, modLen));
	    			}*/
    			}
       		}
       	}
       	if (modBefore.length() == 0) {
       		modBefore.append("None");
       	}
       	if (modAfter.length() == 0) {
       		modAfter.append("None");
       	}
       	return modStr.toString() + "\n" + ms1ModStr.toString() + "\n" + ms2ModStr.toString() + "\n" + modBefore.toString() + "\n" + modAfter.toString();
    }

    public static String padRightBlank(String str, int defaultLen) {
       	StringBuilder sb = new StringBuilder();
       	sb.append(str);
       	int numBlank = defaultLen - str.length();
       	while (numBlank > 0) {
       		sb.append(" ");
       		numBlank -= 1;
       	}
       	return sb.toString();
    }
    
    public static List<String> getModifiers(MethodSig ms) {
       	ArrayList<String> mods = new ArrayList<String>();
       	if (ms.isPublic) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}
       	if (ms.isProtected) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}
       	if (ms.isPrivate) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}
       	if (ms.isDefault) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}
       	if (ms.isStatic) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}
       	if (ms.isAbstract) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}
       	if (ms.isFinal) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}
       	if (ms.isNative) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}
       	if (ms.isHidden) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}
       	if (ms.isDeprecated) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}
       	if (ms.isInternal) {
       		mods.add("Yes");
       	} else {
       		mods.add("No");
       	}

       	return mods;
    }
}
