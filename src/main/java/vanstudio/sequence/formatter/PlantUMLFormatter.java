package vanstudio.sequence.formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vanstudio.sequence.config.SequenceSettingsState;
import vanstudio.sequence.openapi.Constants;
import vanstudio.sequence.openapi.model.CallStack;
import vanstudio.sequence.openapi.model.MethodDescription;

/**
 * Generate <a href="https://plantuml.com/sequence-diagram">PlantUml sequence diagram</a> format.
 */
public class PlantUMLFormatter implements IFormatter {
    @Override
    public String format(CallStack callStack) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("@startuml").append('\n');
        buffer.append("participant Actor").append('\n');
        String classA = callStack.getMethod().getClassDescription().getClassShortName();
        String method = getMethodName(callStack.getMethod());

        boolean showCallNumbers = SequenceSettingsState.getInstance().SHOW_CALL_NUMBERS;

        ArrayList<Integer> seqs = new ArrayList<>();
        String seq = "";
        if (showCallNumbers) {
            seqs.add(1);
            seq = seqs.stream().map(String::valueOf).collect(Collectors.joining("."));

        }

        if (Constants.CONSTRUCTOR_METHOD_NAME.equals(callStack.getMethod().getMethodName())) {
            if (showCallNumbers) {
                buffer.append(seq).append(":");
            }
            buffer.append("create ").append(classA).append('\n');
        }

        buffer.append("Actor").append(" -> ").append(classA).append(" : ");
        if (showCallNumbers) {
            buffer.append(seq).append(":");
        }
        buffer.append(method).append('\n');
        buffer.append("activate ").append(classA).append('\n');
        generate(buffer, callStack, seqs);
        buffer.append("return").append('\n');
        buffer.append("@enduml");
        return buffer.toString();
    }

    private void generate(StringBuffer buffer, CallStack parent, List<Integer> parentSeqs) {
        String classA = parent.getMethod().getClassDescription().getClassShortName();
        boolean showCallNumbers = SequenceSettingsState.getInstance().SHOW_CALL_NUMBERS;

        for (int i = 0; i < parent.getCalls().size(); i++) {
            ArrayList<Integer> innerSeqs = new ArrayList<>();
            String seq = "";
            if (showCallNumbers) {
                innerSeqs.addAll(parentSeqs);
                innerSeqs.add(i + 1);
                seq = innerSeqs.stream().map(String::valueOf).collect(Collectors.joining("."));
            }

            CallStack callStack = parent.getCalls().get(i);
            String classB = callStack.getMethod().getClassDescription().getClassShortName();
            String method = getMethodName(callStack.getMethod());

            if (Constants.CONSTRUCTOR_METHOD_NAME.equals(callStack.getMethod().getMethodName())) {
                if (showCallNumbers) {
                    buffer.append(seq).append(":");
                }
                buffer.append("create ").append(classB).append('\n');
            }
            buffer.append(classA).append(" -> ").append(classB).append(" : ");
            if (showCallNumbers) {
                buffer.append(seq).append(":");
            }
            buffer.append(method).append('\n');
            buffer.append("activate ").append(classB).append('\n');
            generate(buffer, callStack, innerSeqs);
            buffer.append(classB).append(" --> ").append(classA).append('\n');
            buffer.append("deactivate ").append(classB).append('\n');
        }

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
