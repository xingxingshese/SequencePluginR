package vanstudio.sequence.formatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import vanstudio.sequence.config.SequenceSettingsState;
import vanstudio.sequence.openapi.model.CallStack;
import vanstudio.sequence.openapi.model.MethodDescription;

public class SdtFormatter implements IFormatter{
    @Override
    public String format(CallStack callStack) {
        StringBuffer buffer = new StringBuffer();
        generate(buffer, callStack);
        return buffer.toString();
    }

    private void generate(StringBuffer buffer, CallStack parent) {
        ;
        buffer.append('(').append('\n').append(fillCommentToMethodName(parent)).append('\n');
        for (CallStack callStack : parent.getCalls()) {
            generate(buffer, callStack);
        }
        buffer.append(')').append('\n');
    }

    /**
     * 填充方法注释到方法名
     * @param parent
     */
    private String fillCommentToMethodName(CallStack parent) {
        String json = parent.getMethod().toJson();


        // 1. 解析JSON字符串
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String methodName = getMethodName(parent.getMethod());
        // 2. 修改指定字段的值
        jsonObject.addProperty("_methodName", methodName);

        // 3. 生成新的JSON字符串
        String modifiedJson = jsonObject.toString();
        return modifiedJson;

    }

    private String getMethodName(MethodDescription method) {
        if (method == null)
            return "";

        String methodDesc = "";
        if (SequenceSettingsState.getInstance().SHOW_COMMENTS){
            if (method.getMethodDesc() != null && !method.getMethodDesc().isEmpty()) {
                methodDesc = method.getMethodDesc().replace("\r\n", "\n").replace("\n", "\\n") + "\\n";
            }
        }
        if (SequenceSettingsState.getInstance().SHOW_SIMPLIFY_CALL_NAME) {
            return methodDesc + method.getMethodName();
        } else {
            return methodDesc + method.getFullName();
        }

    }
}
