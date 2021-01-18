package visitor;

public final class CCodeString
{
	public static final String deleteSubstring = "char* deleteSubstring(char* str, char* sub)\n" +
												 "{\n" +
												 "    char *tempStr = str;\n" +
												 "    char *result = malloc(sizeof(char) * (strlen(str) + 1));\n" +
												 "\n" +
												 "    while(tempStr != '\\0')\n" +
												 "    {\n" +
												 "        if(strncmp(tempStr, sub, strlen(sub)))\n" +
												 "        {\n" +
												 "            char *dest = malloc(sizeof(char));\n" +
												 "            *dest = *tempStr;\n" +
												 "            result = strcat(result, dest);\n" +
												 "            tempStr++;\n" +
												 "        }\n" +
												 "        else\n" +
												 "        {\n" +
												 "            tempStr += strlen(sub);\n" +
												 "            result = strcat(result, tempStr);\n" +
												 "            break;\n" +
												 "        }\n" +
												 "\n" +
												 "    }\n" +
												 "	  \n" +
												 "    return result;\n" +
												 "}";

	public static final String countOccurrences = "int countOccurrences(char* string, char* substring)\n" +
												  "{\n" +
												  "    int substringLen = strlen(substring);\n" +
												  "    int count = 0;\n" +
												  "    char *tmp = string;\n" +
												  "    \n" +
												  "    if(substringLen) \n" +
												  "        while ((tmp = strstr(tmp, substring))) \n" +
												  "        {\n" +
												  "            tmp += substringLen;\n" +
												  "            count++;\n" +
												  "        }\n" +
			 									  "    \n" +
												  "    return count;\n" +
												  "}";

	public static final String concatString = "char* concatString(char *str1, char *str2)\n" +
					                          "{\n" +
											  "    char *result = malloc(sizeof(char) * (strlen(str1) + strlen(str2) + 1));\n" +
			                                  "    strcat(result, str1);\n" +
			                                  "    strcat(result, str2);\n" +
			                                  "    \n" +
			                                  "    return result;\n" +
			                                  "}";

	public static final String repeatString = "char* repeatString(char *str, int n)\n" +
											  "{\n" +
									          "    char *result = malloc(sizeof(char) * (strlen(str) * n + 1));\n" +
			                                  "    for (int i = 0; i < n; i++)\n" +
			                                  "        result = concatString(result, str);\n" +
			                                  "    \n" +
			                                  "    return result;\n" +
			                                  "}";

	public static final String inputString = "char* inputString()\n" +
											 "{\n" +
											 "    char *result = malloc(sizeof(char));\n" +
											 "    char *ch = malloc(sizeof(char));\n" +
											 "    \n" +
											 "    while((*ch = getchar()) != '\\n')\n" +
											 "        result = concatString(result, ch);\n" +
											 "    \n" +
											 "    return result;\n" +
											 "}\n";
}
