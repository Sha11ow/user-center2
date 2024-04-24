<template>
    <div>
        <el-table :data="myCourses" style="width: 100%" :header-cell-style="{ 'text-align': 'center' }" border>
            <el-table-column prop="course_id" label="课程号" align="center" />
            <el-table-column prop="course_name" label="课程名" align="center" />
            <el-table-column label="绩点" align="center">
                <template v-slot="scope">
                    <div>{{ calculateGPA(scope.row.score) }}</div>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <el-col :span="24" style="text-align: center; font-weight: bold; margin-top: 20px;">
                平均绩点: {{ calculateAverageGPA() }}
            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {
    name: "studentQueryScore",
    props: {
        myCourses: {
            type: Array,
            required: true,
        },
    },
    data() {
        return {
            quizCourses: [
                {
                    course_id: "1",
                    course_name: "123",
                    teacher_name: "123",
                    score: "1",
                },
                {
                    course_id: "2",
                    course_name: "123",
                    teacher_name: "123",
                    score: "2",
                },
                {
                    course_id: "3",
                    course_name: "123",
                    teacher_name: "123",
                    score: "3",
                },
                {
                    course_id: "4",
                    course_name: "123",
                    teacher_name: "123",
                    score: "4",
                }
            ]
        }
    },
    methods: {
        //分数转换为绩点
        calculateGPA(score) {
            if (score >= 90) return '4.0';
            if (score >= 85) return '3.7';
            if (score >= 82) return '3.3';
            if (score >= 78) return '3.0';
            if (score >= 75) return '2.7';
            if (score >= 72) return '2.3';
            if (score >= 69) return '2.0';
            if (score >= 66) return '1.7';
            if (score >= 63) return '1.3';
            if (score >= 60) return '1.0';
            return 0.0;
        },
        //计算平均绩点
        calculateAverageGPA() {
            const totalGPA = this.myCourses.reduce((sum, course) => sum + this.calculateGPA(course.score), 0);
            const averageGPA = totalGPA / this.myCourses.length;
            return averageGPA.toFixed(2); // 保留2位小数
        },
    },
};
</script>

<style scoped></style>