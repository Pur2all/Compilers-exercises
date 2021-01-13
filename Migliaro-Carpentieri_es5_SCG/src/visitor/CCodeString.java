package visitor;

public final class CCodeString
{
	public static final String deleteSubstring = "char* deleteSubstring(char* str, char* sub)\n" +
												 "{\n" +
												 "    char *tempStr;\n" +
												 "    char *result = malloc(sizeof(char) * strlen(str));\n" +
												 "\n" +
												 "    for(tempStr = str; tempStr != '\\0';)\n" +
												 "    {\n" +
												 "        if(strncmp(tempStr, sub, strlen(sub)))\n" +
												 "        {\n" +
												 "            char *dest = malloc(sizeof(char) * 1);\n" +
												 "            *dest = *tempStr;\n" +
												 "            result = strcat(result, dest);\n" +
												 "            tempStr = tempStr + 1;\n" +
												 "        }\n" +
												 "        else\n" +
												 "        {\n" +
												 "            tempStr += strlen(sub);\n" +
												 "            break;\n" +
												 "        }\n" +
												 "\n" +
												 "    }\n" +
												 "    result = strcat(result, tempStr);\n" +
												 "	  \n" +
												 "    return result;\n" +
												 "}";
}
