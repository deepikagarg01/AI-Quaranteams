import {
  Component,
  OnInit,
  AfterContentInit,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { ApiService } from '../api.service';
import {
  StatsData, mockData, areaData, violationCountGraphData1, distanceViolationData1,
  violationCountGraphData2, distanceViolationData2
} from '../stats-data';
import * as Highcharts from 'highcharts';
import { ToastService } from '../_services/toast.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit, AfterContentInit {
  public graphicalData: StatsData[];
  isChangeLocation: boolean;
  public areaData: StatsData[] = areaData;
  public selectedArea: StatsData;
  currentVideo: string;
  isVideoLoaded: boolean;
  isPlay = false;
  isCreateAlert: boolean;
  violationLevel;
  covidSummary;
  isShareOpen: boolean;
  isShareOpen1: boolean;
  isActive;
  isActiveShare;

  lineChart1: any;
  barGraph1: any;
  lineChart2: {};
  barGraph2: {};
  selectedLineChart: {};
  selectedGraph: {};
  Highcharts = Highcharts;
  localities:{};
  socialDistancingDataBasedOnLatLong:any;
  localityToLatitudeLongitudeData:{};
  lat = "28.455";
  long= "77.499";
  fisrtTimePageLoad = 0;
  videoFile: any;
  @ViewChild('videoPlayer', { static: false }) videoplayer: ElementRef;

  constructor(private apiService: ApiService, public toastService: ToastService) {
    this.isChangeLocation = false;
    this.currentVideo = '../../assets/video/Output_SocDis_Area_1.mp4';
    this.isVideoLoaded = true;
    this.violationLevel = 20;
    this.isShareOpen = false;
    this.isShareOpen1 = false;    
    if(this.fisrtTimePageLoad === 0){
      this.apiService.getSocialDistancingDataBasedOnLatLong(this.lat, this.long).subscribe((result)=>{
        this.socialDistancingDataBasedOnLatLong=result;
      }) 
    }
  }

  showSuccess(headerText, successMsg) {
    this.toastService.show(successMsg, {
      classname: 'bg-success text-light',
      delay: 3000,
      autohide: true,
      headertext: headerText
    });
  }

  showError() {
    this.toastService.show('Violation count increasing rapidly in this area. Would you like to share this information with higher authorities', {
      classname: 'bg-danger text-light',
      delay: 2000,
      autohide: false,
      headertext: `Violations Exceeded ALERT !!!`
    });
  }

  public ngOnInit(): void {
    this.showError();
    // mockData
    //  this.graphicalData = mockData;
    this.socialDistancingDataBasedOnLatLong = areaData[1];
    this.selectedArea = areaData[1];
    this.lineChart1 = violationCountGraphData1;
    this.barGraph1 = distanceViolationData1;
    this.lineChart2 = violationCountGraphData2;
    this.barGraph2 = distanceViolationData2;
    this.selectedLineChart = this.lineChart1;
    this.selectedGraph = this.barGraph1;

    this.apiService.getCovidHighlight().subscribe((data) => {
      const res = data;
      this.covidSummary = data;
    });
    this.apiService.getSocialDistancingDataBasedOnLatLong(this.lat, this.long).subscribe((result)=>{
      this.socialDistancingDataBasedOnLatLong=result;
    }) 


    this.renderViolationPerDayGraph(this.lat, this.long);

    this.renderViolationVsDistanceLocalityGraph(this.lat, this.long);

    this.apiService.getAreaWiseZonePercentageForPieChart().subscribe((result)=>{
    })

    this.apiService.getAllLocalities().subscribe((result)=>{
           this.localities = result;
    })

    this.apiService.getLocalitiesMappedLatLong().subscribe((result)=>{
           this.localityToLatitudeLongitudeData = Object.create(result);
    })
    
  }

  ngAfterContentInit() { }

  openChangeLocationPopup() {
    this.isChangeLocation = true;
  }

  closeChangeLocationPopup() {
    this.isChangeLocation = false;
  }
  openCreateAlertPopup() {
    this.isCreateAlert = true;
  }

  closeCreateAlertPopup() {
    this.isCreateAlert = false;
  }

  createAlert() {
    this.showSuccess('Success', 'Alert created sucessfully');
    this.closeCreateAlertPopup();
  }

  shareNow() {
    this.showSuccess('Success', 'Shared successfully !');
  }

  shareIt() {
    this.isShareOpen = !this.isShareOpen;
  }

  shareIt1() {
    this.isShareOpen1 = !this.isShareOpen1;
  }

  closeShareModal() {
    this.isShareOpen = false;
  }
  

  renderViolationPerDayGraph(lat, long){
    this.apiService.getPatternOfViolationAccrossDaysForGraph(lat, long).subscribe((result)=>{
      this.lineChart1.xAxis.categories = result.dates;
      this.lineChart1.series[0].name = result.locality;
      this.lineChart1.series[0].data = result.violations;
      this.selectedLineChart = Object.assign({}, this.lineChart1);

    })
  }

  renderViolationVsDistanceLocalityGraph(lat, long){
    this.apiService.getDistanceToAvgViolationsWithLocationForGraph(lat, long).subscribe((result)=>{
      this.barGraph1.series = result;
      this.selectedGraph = Object.assign({}, this.barGraph1);
    })
  }

  downloadVideo(lat, long){
      this.apiService.downloadVideo(lat, long).subscribe((result)=>{
      this.videoFile=result;
      console.log("videoFile:: ", this.videoFile);
    })
  }

  changeLocation() {
    this.fisrtTimePageLoad = 1;
    var latLong = this.localityToLatitudeLongitudeData[this.selectedArea.locality];
    var res = latLong.split("/")
    this.apiService.getSocialDistancingDataBasedOnLatLong(res[0], res[1]).subscribe((result)=>{
      this.socialDistancingDataBasedOnLatLong=result;
    })
    this.selectedArea = this.selectedArea === areaData[1] ? areaData[0] : areaData[1];

    this.renderViolationPerDayGraph(res[0], res[1]);
    this.renderViolationVsDistanceLocalityGraph(res[0], res[1]);
    this.switchVideo();;
    this.closeChangeLocationPopup();
    this.showSuccess('Success', 'Location has been changed.');
  }

  switchVideo() {
    this.isVideoLoaded = false;
    if (this.selectedArea === areaData[0]) {
      this.isVideoLoaded = true;
      this.currentVideo = '../../assets/video/Output_SocDis_Area_2.mp4';
    } else {
      this.currentVideo = '../../assets/video/Output_SocDis_Area_1.mp4';
    }
  }
}
