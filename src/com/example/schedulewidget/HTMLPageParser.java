package com.example.schedulewidget;

import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Xml;

public class HTMLPageParser
{
    private final String BLOCK_FINDER_PATTERN = "(<div class=\"block\">.*?(\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d).*?</div.*?<!-- block -->)";
    private final String LESSON_DATA_FINDER_PATTERN = "<div class=\"(?:blockbody|blockbody blockchangeday)\" style=\"background:#.*?\">.*?<font.*?>(.*?)<.*?<strong><font.*?>(.*?)<.*?ауд.*?>(.*?)<.*?</font><br.*?>(.*?)<br.*?Д";
    private final String EMPTY_NAMESPASE = "";
    private final String TAG_SCHEDULE = "schedule";
    private final String TAG_DAY = "day";
    private final String TAG_LESSON = "lesson";
    private final String ATTRIBUTE_DATE = "date";
    private final String ATTRIBUTE_TITLE = "title";
    private final String ATTRIBUTE_NUMBER = "number";
    private final String ATTRIBUTE_CLASSROOM = "classroom";
    private final String ATTRIBUTE_TEACHER = "teacher";
    private final String ATTRIBUTE_CREATION_DATE = "creation_date";

    private final int LESSON_TITLE_REGEXGROUP_INDEX = 1;
    private final int LESSON_TYPE_REGEXGROUP_INDEX = 2;
    private final int AUDITORY_REGEXGROUP_INDEX = 3;
    private final int TEACHER_REGEXGROUP_INDEX = 4;
    private final int DATE_REGEXGROUP_INDEX = 2;
    private final int BLOCK_REGEXGROUP_INDEX = 1;

    private void WriteStringToXML(String targetString) throws Exception
    {
        ScheduleWidget.getAppContext();
        FileOutputStream fileOutputStream = ScheduleWidget.getAppContext().openFileOutput(GlobalVariables.SCHEDULE_FILE_NAME,
                Context.MODE_PRIVATE);

        fileOutputStream.write(targetString.getBytes());
    }

    public void ParsePage(String inputPage) throws Exception
    {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();

        xmlSerializer.setOutput(stringWriter);

        // start DOCUMENT
        xmlSerializer.startDocument("UTF-8", true);
        // open tag: <schedule>
        xmlSerializer.startTag(EMPTY_NAMESPASE, TAG_SCHEDULE);
        // add creation date
        xmlSerializer.attribute(EMPTY_NAMESPASE, ATTRIBUTE_CREATION_DATE, Utility.GetCurrentDate(GlobalVariables.DATE_FORMAT));

        Pattern patternForBlock = null;
        patternForBlock = Pattern.compile(BLOCK_FINDER_PATTERN, Pattern.DOTALL);
        Matcher matcherForBlock = patternForBlock.matcher(inputPage);

        String block = "";
        String date = "";
        Pattern patternForLessonData = Pattern.compile(LESSON_DATA_FINDER_PATTERN, Pattern.DOTALL);
        Matcher matcherForLessonData;
        while ( matcherForBlock.find() )
        {
            block = matcherForBlock.group(BLOCK_REGEXGROUP_INDEX);
            date = matcherForBlock.group(DATE_REGEXGROUP_INDEX);

            matcherForLessonData = patternForLessonData.matcher(block);

            // get count of matches
            int matchesCount = 0;
            while ( matcherForLessonData.find() )
            {
                matchesCount++;
            }
            matcherForLessonData.reset();

            String lessonTitle = "";
            String lessonType = "";
            String auditory = "";
            String teacher = "";
            for ( int lessonsCounter = 1; matcherForLessonData.find(); lessonsCounter++ )
            {
                lessonTitle = matcherForLessonData.group(LESSON_TITLE_REGEXGROUP_INDEX);

                lessonType = " [" + matcherForLessonData.group(LESSON_TYPE_REGEXGROUP_INDEX) + "]";

                auditory = matcherForLessonData.group(AUDITORY_REGEXGROUP_INDEX);

                teacher = matcherForLessonData.group(TEACHER_REGEXGROUP_INDEX);

                // Log.v("", lessonTitle + GlobalVariables.NEXT_LINE_CHAR + lessonType + GlobalVariables.NEXT_LINE_CHAR + auditory
                // + GlobalVariables.NEXT_LINE_CHAR + teacher);
                // Log.v("------------", "------------");

                if ( lessonsCounter == 1 )
                {
                    // open tag: <day>
                    xmlSerializer.startTag(EMPTY_NAMESPASE, TAG_DAY);
                    xmlSerializer.attribute(EMPTY_NAMESPASE, ATTRIBUTE_DATE, date);
                }

                // open tag: <lesson>
                xmlSerializer.startTag(EMPTY_NAMESPASE, TAG_LESSON);
                xmlSerializer.attribute(EMPTY_NAMESPASE, ATTRIBUTE_NUMBER, String.valueOf(lessonsCounter));
                xmlSerializer.attribute(EMPTY_NAMESPASE, ATTRIBUTE_TITLE, lessonTitle + lessonType);
                xmlSerializer.attribute(EMPTY_NAMESPASE, ATTRIBUTE_CLASSROOM, auditory);
                xmlSerializer.attribute(EMPTY_NAMESPASE, ATTRIBUTE_TEACHER, teacher);
                // close tag <lesson>
                xmlSerializer.endTag(EMPTY_NAMESPASE, TAG_LESSON);

                // close tag <day> if end
                if ( matchesCount == lessonsCounter )
                {
                    xmlSerializer.endTag(EMPTY_NAMESPASE, TAG_DAY);
                }
            }
        }

        // close tag: <schedule>
        xmlSerializer.endTag(EMPTY_NAMESPASE, TAG_SCHEDULE);

        // close DOCUMENT
        xmlSerializer.endDocument();

        String xmlDoc = stringWriter.toString();
        WriteStringToXML(xmlDoc);
    }
}
