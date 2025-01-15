package com.capstone.project.chronos.jobscheduling.service;

import com.capstone.project.chronos.jobscheduling.model.FitnessAndNutritionAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class FitnessAndNutritionAdviceService {
    private static final List<String> FITNESS_ACTIVITIES = Arrays.asList(
            "30 minutes of jogging or brisk walking, 3 sets of 10 push-ups, 15 squats, and 30-second planks.",
            "15 minutes of yoga (Sun Salutation), 10 minutes of stretching, and 20 minutes of cycling.",
            "10 push-ups, 20 squats, 15 burpees, and 10 minutes of jumping rope.",
            "Go for a 20-minute bike ride, followed by 10 minutes of high knees and 3 sets of 15 crunches.",
            "15 minutes of HIIT (High Intensity Interval Training): 30 seconds sprint, 30 seconds rest for 10 rounds."
    );

    private static final List<String> NUTRITION_ADVICE = Arrays.asList(
            "Breakfast: Poha with a glass of fresh orange juice. Lunch: Dal Tadka with brown rice and cucumber salad. Dinner: Grilled paneer with sautéed spinach and whole wheat roti.",
            "Breakfast: Vegetable upma with coconut chutney. Lunch: Aloo Gobi with chapati and a side of curd. Dinner: Moong dal soup with a vegetable salad.",
            "Breakfast: Paratha with curd and pickle. Lunch: Rajma with rice and a small bowl of mixed fruit salad. Dinner: Stir-fried vegetables with tofu and quinoa.",
            "Breakfast: Masala oats with a boiled egg. Lunch: Chole with basmati rice and a side of green salad. Dinner: Mixed vegetable curry with chapati.",
            "Breakfast: Vegetable sandwich with mint chutney. Lunch: Paneer butter masala with naan and raita. Dinner: Grilled fish with sautéed vegetables."
    );

    private final Random random = new Random();

    public FitnessAndNutritionAdvice getDailyAdvice() {
        // Select random fitness activity
        String fitnessActivity = FITNESS_ACTIVITIES.get(random.nextInt(FITNESS_ACTIVITIES.size()));

        // Select random nutrition advice (with full meal plan)
        String nutritionAdvice = NUTRITION_ADVICE.get(random.nextInt(NUTRITION_ADVICE.size()));

        FitnessAndNutritionAdvice advice = new FitnessAndNutritionAdvice();
        advice.setFitnessActivity(fitnessActivity);
        advice.setNutritionAdvice(nutritionAdvice);

        return advice;
    }
}
